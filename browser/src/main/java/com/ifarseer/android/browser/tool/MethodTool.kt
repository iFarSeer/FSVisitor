package com.ifarseer.android.browser.tool

import com.ifarseer.android.browser.FSBrowserJSCallback
import java.lang.reflect.Method

/**
 * class description here
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 25/01/2018.
 */
class MethodTool {

    companion object {

        fun generateSignature(method: Method): String {
            var methodName = method.name
            var returnType = method.returnType
            var paramTypes = method.parameterTypes
            return buildSignature(methodName, returnType, paramTypes)
        }

        fun buildSignature(methodName: String, returnType: Class<*>, paramTypes: Array<Class<*>>): String {

            val builder = StringBuilder()
            builder.append(returnTypeToChar(returnType))
            builder.append('.')
            builder.append(methodName)
            builder.append('.')

            for (i in paramTypes.indices) {
                val paramClass = paramTypes[i]
                if (paramClass == FSBrowserJSCallback::class.java) {
                    if (i != paramTypes.size - 1) {
                        throw RuntimeException(
                                "FSBrowserJSCallback must be used as last parameter only")
                    }
                }
                builder.append(paramTypeToChar(paramClass))
            }

            return builder.toString()
        }

        private fun returnTypeToChar(returnClass: Class<*>): Char {
            // Keep this in sync with MethodInvoker
            val tryCommon = commonTypeToChar(returnClass)
            if (tryCommon != '\u0000') {
                return tryCommon
            }
            return if (returnClass == Void.TYPE) {
                'v'
            } else {
                throw RuntimeException(
                        "Got unknown return class: " + returnClass.simpleName)
            }
        }

        private fun paramTypeToChar(paramClass: Class<*>): Char {
            val tryCommon = commonTypeToChar(paramClass)
            if (tryCommon != '\u0000') {
                return tryCommon
            }
            return if (paramClass == FSBrowserJSCallback::class.java) {
                'P'
            } else {
                throw RuntimeException(
                        "Got unknown param class: " + paramClass.simpleName)
            }
        }

        private fun commonTypeToChar(typeClass: Class<*>): Char {
            return if (typeClass == Boolean::class.javaPrimitiveType) {
                'z'
            } else if (typeClass == Boolean::class.java) {
                'Z'
            } else if (typeClass == Int::class.javaPrimitiveType) {
                'i'
            } else if (typeClass == Int::class.java) {
                'I'
            } else if (typeClass == Double::class.javaPrimitiveType) {
                'd'
            } else if (typeClass == Double::class.java) {
                'D'
            } else if (typeClass == Float::class.javaPrimitiveType) {
                'f'
            } else if (typeClass == Float::class.java) {
                'F'
            } else if (typeClass == String::class.java) {
                'S'
            } else {
                '\u0000'
            }
        }
    }
}