package com.github.noonesthere.metaannotationsresolver.discoverer

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.spring.model.CommonSpringBean
import com.intellij.spring.model.jam.contexts.CustomContextJavaBean
import com.intellij.spring.model.custom.CustomModuleComponentsDiscoverer

class MetaAnnotationStereotypeDiscoverer : CustomModuleComponentsDiscoverer() {

    private val springStereotypeFqns = setOf(
        "org.springframework.stereotype.Component",
        "org.springframework.stereotype.Service",
        "org.springframework.stereotype.Repository",
        "org.springframework.stereotype.Controller",
        "org.springframework.stereotype.Configuration"
    )

    override fun getCustomComponents(p0: Module): Collection<CommonSpringBean?> {
        TODO("Not yet implemented")
    }

    override fun getProviderName(): String {
        TODO("Not yet implemented")
    }

    override fun getDependencies(p0: Module): Array<out Any?>? {
        TODO("Not yet implemented")
    }

//    override fun getCustomComponents(module: Module): Collection<CommonSpringBean> {
//        val project = module.project
//        val scope = GlobalSearchScope.moduleScope(module)
//        val cache = PsiShortNamesCache.getInstance(project)
//        val beans = mutableListOf<CommonSpringBean>()
//
//        for (className in cache.allClassNames) {
//            val classes = cache.getClassesByName(className, scope)
//            for (psiClass in classes) {
//                val hasMetaAnnotation = psiClass.annotations.any { isMetaAnnotatedWithSpringStereotype(it) }
//                if (hasMetaAnnotation) {
//                    // Create a bean that IDE can navigate
//                    val bean = CustomContextJavaBean(psiClass, this)
//                    beans.add(bean)
//                }
//            }
//        }
//
//        return beans
//    }
//
//    override fun getProviderName(): String = "MetaAnnotationStereotypeDiscoverer"
//
//    override fun getDependencies(module: Module): Array<Any>? = null
//
//    private fun isMetaAnnotatedWithSpringStereotype(annotation: PsiAnnotation): Boolean {
//        val resolvedClass = annotation.resolveAnnotationType() ?: return false
//        return resolvedClass.annotations.any { meta ->
//            springStereotypeFqns.contains(meta.qualifiedName)
//        }
//    }
}
