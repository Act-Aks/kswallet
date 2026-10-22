# KsWallet R8 rules.
#
# Minification is off for now (see the convention plugin): add the keep rules for any
# reflection-based library *before* enabling it, not after the release build crashes.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Type-safe Navigation serialises destinations reflectively.
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
