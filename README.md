# KJect
The Kotlin Injector 💉

## License
KJect is licensed under the [Apache 2.0 License](LICENSE).

## Contributing
If you want to contribute to KJect, feel free to create a pull request.
See [CONTRIBUTING.md](CONTRIBUTING.md) for more information.

## What is KJect?
KJect is an easy-to-use injection framework for Kotlin.

In addition to it's high configurability, KJect also supports suspending functions and coroutines.

## How to use KJect
### 1. Import KJect
Currently, KJect is not available on Maven Central. You can either
- Download the source code and publish it to your local Maven repository, or
- Download the latest release from the releases page.

#### Local Maven Repository
To clone the repository, run
```shell
git clone https://github.com/CheeseTastisch/KJect.git
```

and then execute
```shell
./gradlew publishToMavenLocal
```

After that you can add the following to your `build.gradle.kts`:
```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("me.kject:KJect:1.0.0")
}
```

#### Download from Releases
You can download the latest release from the [releases page](https://github.com/CheeseTastisch/KJect/releases).

After that you can add the following to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation(files("path/to/KJect.jar"))
}
```

### 2. Initialize KJect
To initialize KJect, call `KJect.launch()`:
```kotlin
KJect.launch(coroutineScope)
```

After that you can start using all features of KJect.
The `coroutineScope` parameter must be a coroutine scope, that stays alive as long as you want to use KJect.

### 3. Use KJect
#### Create a class
First create a class that you want to inject. For example:
```kotlin
class TestClass {
    
    fun test() {
        println("Hello World!")
    }
    
}
```

#### Create a function
Next, create a function that uses the class you just created.
Add the `TestClass` as a parameter and add the `@Inject` annotation to it.

For example:
```kotlin
fun testFunction(@Inject testClass: TestClass) {
    testClass.test()
}
```

#### Call the function through KJect
Now you can call the function through KJect:
```kotlin
KJect.call(::testFunction)
```

A more detailed documentation on how KJect works, will be available soon.

### 4. Dispose KJect
To dispose KJect, call `KJect.dispose()`:
```kotlin
KJect.dispose()
```