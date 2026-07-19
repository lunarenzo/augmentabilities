<div align="center" style="display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;">
  <img align="center" style="text-align:center;" src="docs/assets/example_banner.webp" alt="project banner">

  <h1>AugmentAbilities</h1>

  _A powerful Minecraft plugin that grants special capabilities, custom attributes, and passive traits to players._

  <br>
  <div>
    <a href="LICENSE">
      <img alt="License" src="https://img.shields.io/github/license/lunatech/AugmentAbilities?style=for-the-badge&color=blue&labelColor=141417">
    </a>
    <img alt="Latest Version" src="https://img.shields.io/github/v/release/lunatech/AugmentAbilities?include_prereleases&sort=semver&style=for-the-badge&label=LATEST%20VERSION&labelColor=141417">
    <img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/lunatech/AugmentAbilities/ci.yml?style=for-the-badge&labelColor=141417">
  </div>
</div>

---

## 🌟 Features

- **Modular Augmentations**: Dynamically assign and manage custom abilities for players.
- **Robust Configuration**: Clean, well-structured configuration files powered by Crate (YAML/JSON).
- **Database Integration**: Fully integrated database layer using flyway migrations, HikariCP, and type-safe jOOQ queries.
- **Multi-Module Architecture**: Built with a clean separation of concerns (`api`, `common`, `paper`).
- **Developer API**: Exposes a flexible API to let other plugins hook in and retrieve active player abilities.

---

## 🚧 API for Developers

We provide an API for developers to easily interface with player abilities.

### Gradle Kotlin DSL

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.lunatech:augmentabilities-api:VERSION")
}
```

### Maven

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>com.lunatech</groupId>
            <artifactId>augmentabilities-api</artifactId>
            <version>VERSION</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

---

## 🔧 Contributing

Contributions are welcome! Please make sure to read our [Contributor's Guide](docs/CONTRIBUTING.md) and [Code of Conduct](docs/CODE_OF_CONDUCT.md) before submitting a pull request.

---

## 📝 Licensing

This project is licensed under the MIT No Attribution License - see the [LICENSE](LICENSE) file for details.
