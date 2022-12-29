# Quark I18N
[![Build](https://github.com/coditory/quark-i18n/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/quark-i18n/actions/workflows/build.yml)
[![Coverage Status](https://coveralls.io/repos/github/coditory/quark-i18n/badge.svg)](https://coveralls.io/github/coditory/quark-i18n)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.quark/quark-i18n/badge.svg)](https://mvnrepository.com/artifact/com.coditory.quark/quark-i18n)

## Additional features
- Automatic type based formatting (*)
- ICU integration
- Message references
- Flexibility:
  - split big message file into multiple smaller
  - ...or use single to define messages for multiple locales
- Supports multiple file formats: yaml, properties, json (*)
- Dev mode

## Installation

Add to your `build.gradle`:

```gradle
dependencies {
    implementation "com.coditory.quark:quark-i18n:0.0.1"
}
```

## TODO
- Fix TODOs in code
- Test
- Unify exceptions
- Unify public and final modifiers
- Update readme
- Release