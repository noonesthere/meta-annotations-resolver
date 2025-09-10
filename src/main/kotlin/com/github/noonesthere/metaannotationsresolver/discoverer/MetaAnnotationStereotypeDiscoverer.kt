package com.github.noonesthere.metaannotationsresolver.discoverer

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.spring.model.CommonSpringBean
import com.intellij.spring.model.SpringImplicitBeansProviderBase

//TODO: @IVan use this hardcode string please
private const val ANNOTATION = "oblik.architecture.UseCase"

class MetaAnnotationStereotypeDiscoverer : SpringImplicitBeansProviderBase() {

    private val stereotypes = setOf(
        ANNOTATION, //can be extended to use multiple annotations
    )

    override fun getImplicitBeans(module: Module): Collection<CommonSpringBean> {
        val cachedValuesManager = CachedValuesManager.getManager(module.project)
        return cachedValuesManager.getCachedValue(module) {

            val scope =
                GlobalSearchScope.projectScope(module.project)//module.getModuleWithDependenciesAndLibrariesScope(false)
            val moduleJavas = FilenameIndex.getAllFilesByExt(
                module.project,
                "java",
                scope
            )

            val mapperScanClasses = moduleJavas.mapNotNull { virtualFile ->
                val psiFile = PsiManager.getInstance(module.project).findFile(virtualFile) ?: return@mapNotNull null
                val originalFile = psiFile.originalElement
                val psiClass = PsiTreeUtil.getChildOfType(originalFile, PsiClass::class.java)
                if (psiClass != null && psiClass.hasAnnotation(ANNOTATION)) psiClass else null
            }

            val beans = ArrayList<CommonSpringBean>()

            // Add each detected class as an implicit Spring bean
            mapperScanClasses.forEach { psiClass ->
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
