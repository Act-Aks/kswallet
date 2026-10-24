package com.kswallet.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * Creates a [SerializersModule] that registers [routes] as polymorphic
 * subclasses of [NavKey].
 *
 * Each route must have a serializer discoverable through
 * [KClass.serializer], typically generated using `@Serializable`.
 *
 * Supports both singleton routes and routes with constructor parameters:
 *
 * ```
 * val homeModule = navigationSerializersModule(
 *     HomeRoute::class,
 * )
 *
 * val categoriesModule = navigationSerializersModule(
 *     CategoriesRoute::class,
 *     CategoryEditorRoute::class,
 * )
 * ```
 *
 * This function uses internal serializer lookup APIs. Route classes with
 * generic type parameters are not supported by this helper.
 *
 * @param routes Concrete navigation route classes to register.
 * @return A serializers module containing the supplied route registrations.
 * @throws kotlinx.serialization.SerializationException If a serializer
 * cannot be found for a supplied route.
 */
fun navigationSerializersModule(
    vararg routes: KClass<out NavKey>,
): SerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        routes.forEach { route ->
            registerRoute(route)
        }
    }
}

/**
 * Registers [route] with its serializer while preserving the relationship
 * between the route's class type and its serializer type.
 */
@OptIn(InternalSerializationApi::class)
private fun <T : NavKey> PolymorphicModuleBuilder<NavKey>.registerRoute(
    route: KClass<T>,
) {
    subclass(route, route.serializer())
}
