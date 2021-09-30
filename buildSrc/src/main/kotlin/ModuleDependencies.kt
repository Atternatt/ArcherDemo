const val app = ":FreshlyPressed"
private const val domain = ":domain"
private const val arch = ":arch"


object AppModule {
    //Contains app Dependency Injection
    val dependsOn = listOf(domain, arch)
}

object DomainModule {
    val dependsOn = listOf(arch)
}
