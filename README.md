## _Atternatt's_ Archer Demo documentation
 
 
> This is a simple app resolving a simple feature: __An App that lists the most recent stories from WordPress.com Discover that contains featured images__. The purpose of this code is not only displaying a UI but also providing a structured architecture in a **Clean** way using an **arch** module that abstracts the implementation for a _Clean Architecture_. This module will be published separately as a Gradle dependency in the future but as a
demonstration purpose it is added as another project module. It's name is `Archer` because internally uses [Arrow](https://arrow-kt.io) as a functional framerwork tool.

> This project may look overenginered for the small feature that is implementing but the purpose of the project is just demonstrating how it would work in a larger projects where scaling is important.
 
## Table of content
1. [Result](#result)
2. [Dependency Management](#dependency-management)
   * [Gradle](#gradle)
3. [Architecture](#architecture)
   * [Unidirectional Data Flow](#unidirectional-data-flow)
4. [Arch Module](#arch-module)
   * [Either](#either)
   * [DataSource](#datasource)
       * [Queries](#queries)
   * [Repository](#repository)
       * [Operation](#operation)
   * [Failure](#failure)
   * [Mapper](#mapper)
       * [DataSource and Repository Mapping](#datasource-and-repository-mapping)
       * [Transformation](#transformation)
   * [UseCase](#usecase)
5. [Domain](#domain)
   * [UseCase and ViewModel](#usecase-and-viewmodel)
       * [Package data](#package-data)
       * [Package di](#package-di)
       * [Package mapper](#package-mapper)
       * [Package model](#package-model)
       * [Package query](#package-query)
       * [Package usecase](#package-usecase)
       * [Package viewmodel](#package-viewmodel)
   * [State](#state)
6. [FreshlyPressed](#freshlypressed)
   * [Interesting things](#interesting-things)
       * [Coroutines and Palette](#coroutines-and-palette)
       * [CollapsibleToolbar](#collapsibletoolbar)
7. [BuildSrc](#buildsrc)
8. [Testing](#testing)
   * [Network](#network)
   * [Database](#database)
 
 
## Result
>This is the resulting app (Take in consideration that the resolution is not the real one due to limitations of file size).
 
The app contains 2 screens.
1. **Loading Screen**: Shows an animated cup of tea with several changing texts while fetching the data to display.
2. **Posts Screen**: A List of posts
  * The header will collapse with a animation taking the scroll of the list as anchor.
  * The first item displayed will collapse to give space to the toolbar and will expand while scrolling.
  * Each item in the list has a gradient background above the title and body that it's tinted taking the main color of the featured image of each post.
  * Each item contains a **Post** Information including: _Title_, _Body_, _Author_ and _Number of Subscribers_
 
| Loading   |      Loaded      |
|----------|:-------------:|
| <img src="https://user-images.githubusercontent.com/2378636/115068202-3990d780-9ef2-11eb-9307-d5dc5b9a29ef.gif" alt="drawing" width="400"/>| <img src="https://user-images.githubusercontent.com/2378636/115068402-82489080-9ef2-11eb-83ac-c9e96217212f.gif" alt="drawing" width="400"/>|
 
## Dependency Management
 
### Gradle
 
In this project we will be using Gradle with Kotlin Script (kts). All the dependencies are in the `buildSrc` folder grouped by their function. So it's easy to use and less verbose.
 
## Architecture
 
This project is following a Clean architecture, along with MVVM for Presentation layer.
 
The project is divided with different modules:
- **FrashlyPressed**: Contains the app initialization with all the screens.
- **Domain**: Contains all the business logic. It contains the ViewModels, that exposes the behavior to the UI, The use cases and all the datasources that will make real estate calls to database and API's and will also provide the Dependency Injection modules that will provide them.
- **Arch**: It's an abstraction for clean architecture. Providing all the required classes that are usually involved in this architecture Like **UseCase, Repository and DataSource** and some other handy helper objects.
- **BuildSrc**: It's a module that will share basig gradle dependencies over all modules. Working with gradle kts files.
 
![Arch](https://user-images.githubusercontent.com/2378636/115083069-24727380-9f07-11eb-8b46-0405bcdcae59.jpg)
 
### Unidirectional Data Flow
 
This concept means that all the data flow goes in a single direction. From the lower layer `DataSource` to the upper one `Fragments`. The communication through layers in an upper direction is made through events. Each layer will expose their dependencies (following the dependency inversion principle).
 
![Lienzo 2](https://user-images.githubusercontent.com/2378636/115085233-6ea92400-9f0a-11eb-918e-d506945047f6.jpg)
 
##  Arch Module
 
This module contains all the architecture related elements. It's purpose it's to provide all the boilerplate behavior usually implemented in _Clean Architecture_. In the following section we are going to describe all the components inside this module
 
### Either
 
[Either](https://arrow-kt.io/docs/apidocs/arrow-core/arrow.core/-either/index.html#either) is a Functional type inside [Arrow's](https://arrow-kt.io/) library. It follows monad comprehensions and is a wrapper for either a success value or a failure over an operation. It's totally operable with coroutines and can handle concurrency problems easily.
 
It's divided in 2 branches Left and Right, they are mutually exclusives. The regular conventions is to treat the left side of the object as a failure and the right one as a success. As the `Either` short circuits on failure we can always work with it like if we are working with our expected values (Right side).
 
An example of parallel execution with either and coroutines in the project is inside `DefaultGetPostsUseCase.kt`:
```
either {
           val posts = !repository.getAll(query = query, operation = operation)
           posts
               .filter { it.featuredImage != null } //we just want posts with featured images
               .parTraverse { post ->
                   val host: String? = try {
                       URI.create(post.authorUrl).host
                   } catch (e: IllegalArgumentException) {
                       null
                   }
                   if (host.isNullOrBlank()) {
                       post
                   } else {
                       post.copy(
                           numberOfSubscribers = !getsubsCountUseCase(
                               SubscribersQuery(host)
                           ).handleError { 0L })
                   }
               }
       }
 
```
 
The first `either` is a DSL function that creates a coroutine that lets us work imperatively without caring about monad comprehensions. In this case the operator `!` (`not`) is calling a `bind()` function that unwraps the returning `Either` of the repository. After that we are concurrently mapping each `Post` a `parTraverse` operator that it's essentially a `map` function that runs in parallel for each element.
 
For more information on Comprehensions over Coroutines follow this [Link](https://arrow-kt.io/docs/patterns/monad_comprehensions/#comprehensions-over-coroutines).
 
For more information about `parTraverse` follow this [Link](https://arrow-kt.io/docs/fx/async/#partraverse).
 
### DataSource
 
This object represents a source of data. In this project we have 2 common implementations: One wrapping API calls and another one wrapping Database calls for each model that we have. In this case we only provide datasorces for `Posts`.
 
Follow the [Single Responsibility Principle](https://en.wikipedia.org/wiki/Single-responsibility_principle) and [Interface Segregation Principle](https://en.wikipedia.org/wiki/Interface_segregation_principle) a Datasource is fragmented in 3 different interfaces, where `T` is a generic type:
1. `GetDataSource<T>` -> providing get|getAll methods
2. `PutDataSource<T>` -> providing put|putAll methods
3. `DeleteDataSource<T>` -> providing delete|delete All methods
 
All three interfaces together compose a [CRUD](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) pattern. Segregating each interface allows us to implement just the concrete functionalities that we want.
As a example we can take the current apps behavior. We implemented a simple cache using database that never delete their tems so we haven't implemented the `DeleteDataSource`.
 
In out `PostsModule.kt` file we can see how we are creating a `DataSource` without Delete feature:
```
DataSourceMapper<PostDBO, Post>(
           getDataSource = databaseDatasource,
           putDataSource = databaseDatasource,
           deleteDataSource = VoidDeleteDataSource(), // -> We are not providing a DeleteDataSource here.
           toOutMapper = PostDboToPostMapper,
           toInMapper = PostToPostDboMapper
       )
```
 
We are going to talk about [DataSourceMapper](#datasource-and-repository-mapping) later in this document.
 
There are built in `DataSources` in this module ready to use:
* `DeviceStorageDataSourve.kt` A `DataSource` that uses `SharedPreferences` to store data.
* `InMemoryDataSource.kt` A `DataSource` that uses in-memory variables to store data. It can be commonly used to store data at execution time that we won't persist each time we destroy the app.
 
### Queries
 
Following the [Dependency Inversion Principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle) Queries are the main communication point of the `DataSoures`. They are abstractions of data that will be provided from higher layers of the architecture.
 
There are some generic `Queries` already provided like `IdQuery`, `KeyQuery` or `VoidQuery` but commonly a developer should create their own one with the data he/she wants to read.
 
An example of `Query` implemented in the app is `PostsQuery.kt` that holds the parameters we need in order to retrieve a list of `Posts`
 
```data class PostsQuery(val forceRefresh: Boolean, val number: Int = 10) : Query()```
 
### Repository
A `Repository` is a data structure meant to hold one or several DataSources and Bridge the communication with higher level objects. A Repository exposes a [Operation](#operation) object that will be used to determine where do we want/need the information to come from.
 
There is a `CacheRepository.kt`, already provided in the module, that provides cache logic implementation using `Operations`. The developer only needs to provide the required `DataSources` in order to apply it's behaviour.
 
> Remember that everything works assuming that not every dependency can be satisfied and the developer can provide `VoidDataSources` or any `Void-*` object as a parameter if he/she doesn't want to provide an implementation.
 
An example of how to create a `CacheRepository.kt`:
```
CacheRepository(
           getMain = networkDataSourceMappaer,
           putMain = VoidPutDataSource(),
           deleteMain = VoidDeleteDataSource(),
           getCache = databaseDataSourceMapper,
           putCache = databaseDataSourceMapper,
           deleteCache = VoidDeleteDataSource()
       )
```
 
> Notice that there are 2 different types of sources; `main` and `cache`. Where main would be, usually, a remote datasource (like a network one) and the second one will be a in-memory datasource or database.
In any case they are abstractions and the developer is free to implement their prefered behaviours.
 
In this example we don't provide inserts in network (`main`) nor removals in database (`cache`)
 
### Operation
Operations have been introduced in [Repository](#repository) section. They are ID ojects that will be used for the `Repository` in order to select the datasource that will provide the data.
The project provides built in `Operations`:
* DefaultOperation -> A default operation without specific behaviour
* MainOperation -> Data stream will only use the main data source
* MainSyncOperation -> Data stream will use the main data source and then sync result with the cache data source
* CacheOperation -> Data stream will only use the cache data source
* CacheSyncOperation -> Data stream will use the cache data source and sync with the main data source
 
The `CacheRepository` uses actively this `Operators`, as a example we will show how a `get()` is performed:
 
```
when (operation) {
           is DefaultOperation -> get(query, CacheSyncOperation)
           is MainOperation -> getMain.get(query)
           is CacheOperation -> getCache.get(query)
           is MainSyncOperation -> getMain.get(query)
               .flatMap { putCache.put(query, it) }
               .handleErrorWith { failure ->
                   when (failure) {
                       is Failure.NoConnection, is Failure.ServerError -> {
                           get(query, CacheOperation)
                               .mapLeft { failure }
                       }
                       else -> Either.Left(failure)
                   }
               }
           is CacheSyncOperation -> {
               return getCache.get(query).handleErrorWith {
                   when (it) {
                       is Failure.DataNotFound -> get(query, MainSyncOperation)
                       else -> Either.left(it)
                   }
               }
           }
       }
```
### Failure
`Failure` are Exception abstractions. They are used to scope the exception in a closed environment. There are 8 different types of failure:
1. `DataNotFound` -> Data can't be found
2. `DataEmpty` -> Data that we are passing in a lower layer is empty (for instance `null` or an empty list of objects)
3. `NoConnection` -> We can't connect
4. `ServerError` -> A server error happened
5. `QueryNotSupported` -> the query that we are passing is not valid
6. `InvalidObject` -> Data passed is not valid or has been invalidated
7. `UnsupportedOperation` -> the operation that we are using in a repository is not supported
8. `Unknown` -> an unhandled exception wrapper.
 
### Mapper
`Mappers` are interfaces used to transform data between layers. They allow you to isolate implementation over objects like `DataSource` and/or `Repositories` without coupling them. They also implement a simple `(T) -> R` function.
 
#### DataSource and Repository Mapping
Thanks to mappers we provided several [Adapter](https://refactoring.guru/design-patterns/adapter) objects to easily transition between models. Take a look into:
* [DataSourceMapper.kt](arch/src/main/java/com/m2f/arch/data/datasource/DataSourceMapper.kt)
* [RepositoryMapper.kt](arch/src/main/java/com/m2f/arch/data/repository/RepositoryMapper.kt)
 
#### Transformation
Each `DataSource` and `Repository` contains transformation methods to help create a data stack easily. Sometimes we just need a `Repository` that only contains a single `DataSource`. To preserve the architecture integrity we have `DataSource.toXRepository()` and `DataSource.withMapping(Mapper)`. The first one Will create the homologue `Repository` and the second one will create a `DataSourceMapper` with the specified `Mapper`
 
A example of this transformation can be seen in `PostsModule.kt`
```
val networkDatasource: GetDataSource<PostEntity> = GetPostsNetworkDataSource(postsService)
val networkDataSourceMappaer : GetDataSource<Post> = networkDatasource.withMapping(PostEntityToPostMapper)
```
 
or also
 
```
val networkDatasource: GetDataSource<PostEntity> = GetPostsNetworkDataSource(postsService)
val networkDataSourceMappaer : GetDataSource<Post> = networkDatasource.withMapping + PostEntityToPostMapper
```
 
`GetRepository` and `GetDataSource` provides a plus operator with mappers to generate it's mapping homologue.
### UseCase
A `UseCase` represents a business functional requirement. They usually contain a Repository or other `UseCase` and apply any required logic (Like filtering, combination, modification, etc...)
 
There are 2 types of `UseCase`: Parameterized and non parameterized, represented by `ParametrizedUseCase.kt` and `UseCase.kt` respectively. Both of them are abstract classes that follow the [Command Pattern](https://refactoring.guru/design-patterns/command).
The difference between them is that the first one accepts a parameter in it's execution function defined as a [Query](#queries).
 
A `UseCase` is scoped in a coroutine context and it forces a `CoroutineDispatcher` through the constructor.
 
## Domain
This module contains all the business logic of the application. It also contains all the data retrieval logic but this logic is hidden for foreign modules.
 
Domain module only exposes `UseCase` and `ViewModels`.
 
### UseCase and ViewModel
Only the UseCase and ViewModels are exposed outside the module. The purpose of this implementation is to provide a black box of utilities that will be implemented in a UI. Taking in consideration that technologies like [Kotlin Multiplarform](https://kotlinlang.org/docs/multiplatform.html) are appearing we decided to extract presentation logic to this module and share it over other targets,
 
Each feature developed is separated in its own package inside `com.m2f.domain.features`. Each package contain:
 
#### Package data
This package will contain all the `API`'s, [DataSource](#datasource) and `Entities` required for the feature.
>All the elements inside this package are tagged with the `internal` keyword to prevent them from being exposed outside the module.
#### Package di
This package contains the [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) modules that will provide the required `UseCases`.
>As we are working with internal components in the module we are using interfaces provided by [Arch Module](#arch-module) to hide the real implementations.
#### Package mapper
This package will provide all the mapping through `Entities` and **Business Models**.
>This the mappers are tagged with `internal` to prevent them to be exposed
#### Package model
This package exposes the **Business Models** for the feature.
#### Package query
This package exposes the queries.
#### Package usecase
This package provides all the [UseCase](#usecase). In this case we are creating [sealed interfaces](https://kotlinlang.org/docs/sealed-classes.html#try-sealed-interfaces-and-package-wide-hierarchies-of-sealed-classes) for each UseCase to hide the real implementation of the UseCase and prevent foreign modules to implement them.
#### Package viewmodel
This Package exposes all the `ViewModels` related with this feature.
 
### State
As we have seen in [Unidirectional Data Flow](#unidirectional-data-flow) there is a [State](https://refactoring.guru/design-patterns/state) object sent from the `ViewModel`to the `UI`.
A ViewModel provides this object as a wrapper of the data that will be sent to the UI with some other rendering information.
 
This is the implementation of the `State` class:
 
```
sealed class ViewModelState<out T> {
   data class Loading(val isLoading: Boolean) : ViewModelState<Nothing>()
   object Empty : ViewModelState<Nothing>()
   data class Success<T>(val data: T) : ViewModelState<T>()
   data class Error(val failureType: FailureType) : ViewModelState<Nothing>()
}
```
 
It's a simple sealed class with 4 different states,
* one to enable/disable loading
* another to notify that we have empty data
* another one to send the actual data
* and a last one to notify about a error
 
These states are sent from the `ViewModel` through `LiveData`, an Observable object that's aware of the lifecycle of the UI in order to cancel their subscribers observation.
 
## FreshlyPressed
This Module contains all the Screens and UI components of the app. Its composed of two main packages:
1. components: This contain a [CollapsingToolbar](FreshlyPressed/src/main/java/com/automattic/freshlypressed/presentation/components/CollapsibleToolbar.kt) and a modal [loading dialog](FreshlyPressed/src/main/java/com/automattic/freshlypressed/presentation/components/LoadingDialog.kt) both of them can be seen in [Result](#result)
2. features: this package contain each feature that is present in [Domain's](#domain) feature package and contain all the UI related.
 
>This module also provides the entry point for the Dependency Injection: take a look into [MyApplicaton.kt](FreshlyPressed/src/main/java/com/automattic/freshlypressed/MyApplicaton.kt)
 
### Interesting things
#### Coroutines and Palette
Palette is a library that allows identifying color schemes of a bitmap. In order to tint the title gradient of each post we called a Glide's callback to obtain the bitmap and used Palette to retrieve the main color wrapping all this workflow inside a `suspendCancellableCoroutine`.
 
#### CollapsibleToolbar
[CollapsingToolbar](FreshlyPressed/src/main/java/com/automattic/freshlypressed/presentation/components/CollapsibleToolbar.kt) is a custom view that internally combines a [MotionLayout](https://developer.android.com/reference/androidx/constraintlayout/motion/widget/MotionLayout) and a [SeekableAnimatedVectorDrawable](https://developer.android.com/reference/kotlin/androidx/vectordrawable/graphics/drawable/SeekableAnimatedVectorDrawable).
 
<img width="426" alt="Captura de pantalla 2021-04-17 a las 22 04 14" src="https://user-images.githubusercontent.com/2378636/115125516-e5a4f200-9fc8-11eb-8e78-a9762491559d.png">
 
The [vector animation](FreshlyPressed/src/main/res/drawable/header_anim.xml) has been developed through [Shape Shifter Beta](https://beta.shapeshifter.design/).
 
Thanks to the `MotionLayout` we can achieve this kind of parallax-resizing animations:
 
<img src="https://user-images.githubusercontent.com/2378636/115125571-577d3b80-9fc9-11eb-9e0a-e521760053df.gif" alt="drawing" width="300"/>
 
 
## BuildSrc
 
This module contains the dependencies for the other modules. It's composed of 4 files:
- [BaseDependencies.kt](buildSrc/src/main/kotlin/BaseDependencies.kt) -> It provides all the dependencies of the project
- [AppProperties.kt](buildSrc/src/main/kotlin/AppProperties.kt) -> It provides the android properties that will be shared in all modules
- [BuildVariants.kt](buildSrc/src/main/kotlin/BuildVariants.kt) -> It provides BuildVariant information
- [ModuleDependencies.kt](buildSrc/src/main/kotlin/ModuleDependencies.kt) -> It provides intermodule dependencies.
 
## Testing
All modules contain tests but we'll comment on some mocking strategies we have been following in order to isolate behaviors while testing network and database.
### Network
In order to isolate test from the real network layer we have implemented a two custom network Dispatchers [MockNetworkDispatcher](domain/src/test/java/com/m2f/domain/helpers/MockNetworkDispatcher.kt) and [MockNetworkKoDispatcher](domain/src/test/java/com/m2f/domain/helpers/MockNetworkKoDispatcher.kt) for failing responses.
 
This network dispatchers internally use a [MockApiCallFactory](domain/src/test/java/com/m2f/domain/helpers/MockApiCallFactory.kt) that internally create [MockApiCall](domain/src/test/java/com/m2f/domain/helpers/MockApiCall.kt) a interface that works as a Strategy that generates a response. This response is read though a helper function that read stored json files containing the test responses.
 
The json files have been generated using [Postman](https://www.postman.com/) to obtain the real results from the API.
 
We also created a [Decorator](https://refactoring.guru/design-patterns/decorator) for `Dispatchers` named [ModifierDispatcher](domain/src/test/java/com/m2f/domain/helpers/ModifierDispatcher.kt) that uses [ResponseModifier](domain/src/test/java/com/m2f/domain/helpers/ResponseModifier.kt) to add extra behavior to the network responses.
 
An example is [TimeoutModifier](domain/src/test/java/com/m2f/domain/helpers/ResponseModifier.kt) that forces a timeout on the current response.
 
Example of usage can be found in [GetNumSubscribersNetworkDatasourceTest.kt](domain/src/test/java/com/m2f/domain/features/posts/data/datasource/GetNumSubscribersNetworkDatasourceTest.kt) line 100:
 
```
mockWebServer.dispatcher = dispatcher + TimeoutModifier
```
 
It is overriding the plus operator to create a new `Dispatcher`
 
```
operator fun Dispatcher.plus(modifier: ResponseModifier): Dispatcher {
   return ModifierDispatcher(this, modifier)
}
```
 
### Database
[PostsDatabaseDataSource.kt](domain/src/main/java/com/m2f/domain/features/posts/data/datasource/PostsDatabaseDataSource.kt) has a particularity. To preserve data integrity over read/writes we added a `Mutex` in it, locking the acces of the get and put for a thread when another one is performing one of these operations. To test that, in fact, this is working we implemented a special test following this [kotlin feed](https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html#the-problem) that consist of creating several coroutines and check that everything has been performed as expected.
 
In order to isolate the test we create a `JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)` for each test that will be recreated each time.
 

