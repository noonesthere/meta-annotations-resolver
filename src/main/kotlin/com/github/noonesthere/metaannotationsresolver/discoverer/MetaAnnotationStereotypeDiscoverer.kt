package com.github.noonesthere.metaannotationsresolver.discoverer

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.spring.model.CommonSpringBean
import com.intellij.spring.model.SpringImplicitBean
import com.intellij.spring.model.SpringImplicitBeansProviderBase
import org.jetbrains.annotations.NotNull

class MetaAnnotationStereotypeDiscoverer : SpringImplicitBeansProviderBase() {
    private val stereotypes = setOf(
        "org.springframework.stereotype.Component",
        "org.springframework.stereotype.Service",
        "org.springframework.stereotype.Repository",
        "org.springframework.stereotype.Controller",
        "org.springframework.stereotype.Configuration",
        "org.springframework.web.bind.annotation.RestController"
    )

    override fun getImplicitBeans(@NotNull module: Module): Collection<CommonSpringBean> {
        val project = module.project
        val javaFiles = FilenameIndex.getAllFilesByExt(project, "java", module.moduleProductionSourceScope)

        val beans = mutableListOf<CommonSpringBean>()

        for (vf in javaFiles) {
            val psiFile = PsiManager.getInstance(project).findFile(vf) ?: continue
            val psiClass = PsiTreeUtil.getChildOfType(psiFile.originalElement, PsiClass::class.java) ?: continue

            // skip interfaces, enums, annotations
            if (psiClass.isInterface || psiClass.isEnum || psiClass.isAnnotationType) continue

            if (hasMetaStereotype(psiClass)) {
                val beanName = psiClass.name?.replaceFirstChar { it.lowercase() } ?: continue
                beans.add(SpringImplicitBean(getProviderName(), psiClass, beanName))
            }
        }
        return beans
    }

    private fun hasMetaStereotype(psiClass: PsiClass): Boolean {
        for (annotation in psiClass.annotations) {
            val annType = annotation.resolveAnnotationType() ?: continue
            for (meta in annType.annotations) {
                if (meta.qualifiedName in stereotypes) {
                    return true
                }
            }
        }
        return false
    }

    override fun getProviderName(): String = "MetaAnnotation Bean Provider"
}
