package com.github.noonesthere.metaannotationsresolver.discoverer

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.spring.model.CommonSpringBean
import com.intellij.spring.model.SpringImplicitBeansProviderBase
import com.intellij.spring.search.SpringGlobalSearchScopes

class MetaAnnotationStereotypeDiscoverer : SpringImplicitBeansProviderBase() {

    override fun getImplicitBeans(module: Module): Collection<CommonSpringBean> {
        val cachedValuesManager = CachedValuesManager.getManager(module.project)
        return cachedValuesManager.getCachedValue(module) {
            val scope = SpringGlobalSearchScopes.runtime(module, this.includeTests())
            val javaFiles = FilenameIndex.getAllFilesByExt(module.project, "java", scope)

            // Parse all java files into PsiClasses once
            val psiClasses = javaFiles.mapNotNull { vf ->
                val psiFile = PsiManager.getInstance(module.project).findFile(vf) ?: return@mapNotNull null
                PsiTreeUtil.getChildOfType(psiFile, PsiClass::class.java)
            }

            // Step 1: Collect stereotypes (@interface annotated with @Component)
            val stereotypes = psiClasses
                .filter { it.isAnnotationType }
                .filter { it.annotations.any { ann -> ann.qualifiedName == "org.springframework.stereotype.Component" } }
                .mapNotNull { it.qualifiedName }
                .toSet()

            // Step 2: Collect beans (classes annotated with those stereotypes)
            val beanClasses = psiClasses.filter { psiClass ->
                psiClass.annotations.any { it.qualifiedName in stereotypes }
            }

            val beans = ArrayList<CommonSpringBean>()

            // Add each detected class as an implicit Spring bean
            beanClasses.forEach { psiClass ->
                val qualifiedName = psiClass.qualifiedName ?: return@forEach
                val beanName = psiClass.name?.replaceFirstChar { it.lowercaseChar() } ?: return@forEach
                addImplicitLibraryBean(beans, module, qualifiedName, beanName)
            }
            // Return with proper modification trackers for cache invalidation
            CachedValueProvider.Result.create(beans, getDependencies(module))
        }
    }

    override fun getProviderName(): String = "MetaAnnotation Bean Provider"
}
