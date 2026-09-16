# Kotlin serialization and data models
-keep class com.example.domain.model.** { *; }
-keep class com.example.core.database.entity.** { *; }
-keep class com.example.core.security.** { *; }

# Room entities and generated adapters
-keep class com.example.core.database.** { *; }

# Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class com.example.**_HiltModules* { *; }

# WorkManager workers referenced by class
-keep class com.example.core.worker.** { *; }

# Keep enum serialization values
-keepclassmembers enum * {
    <fields>;
}

# Keep Serializable metadata when needed
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

