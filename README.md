# Gentleman

_Этот джентльмен был известен своей изысканной вежливостью и непревзойденной учтивостью. Его запросы
о разрешениях всегда сопровождались глубоким уважением и тактом, он не просто спрашивал, но и
проявлял искренний интерес к мнению других. Его выразительные иконки и стили текста, сопровождающие
любое обращение, создавали приятную атмосферу, будь то отправка уведомлений или доступ к
геолокации._

---

* Простой и элегантный dsl синтаксис
* Запрос одного или нескольких разрешений
* Получение результата в виде коллбэка
* Полная кастомизация интерфейса
* Различные стратегии использования

![Sample](https://github.com/FabitMobile/Gentleman/raw/main/sample/sample.gif)

## Использование

[![](https://www.jitpack.io/v/FabitMobile/Gentleman.svg)](https://www.jitpack.io/#FabitMobile/Gentleman)

Подключение зависимости

project build.gradle.kts

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

module build.gradle.kts

```kotlin
implementation("com.github.FabitMobile.library-appupdate:core:$latestVersion")
```

В удобном месте описываем необходимые разрешения и обработчик результата. Context подойдет любой

```kotlin
Gentleman in Tuxedo {
    with(context)
    ask(Manifest.permission.ACCESS_COARSE_LOCATION)
    await { result ->
        println("granted=${result.granted}; denied=${result.denied}")
    }
}
```

Для запроса нескольких разрешений сразу, указываем их через запятую или в виде списка

Не забываем, что после Adnroid 11 разрешения не запрашиваются, если были отклонены дважды

## Стратегии запросов

### Перезапрос при отклонении

```kotlin
ask(Manifest.permission.ACCESS_COARSE_LOCATION) retry once
```

### Показ Rationale

- Всегда показывать перед запросом

```kotlin
gentle manner ask(Manifest.permission.ACCESS_COARSE_LOCATION)
```

- Показывать при необходимости (поведение по-умолчанию)

```kotlin
usual manner ask(Manifest.permission.ACCESS_COARSE_LOCATION)
```

- Никогда не показывать

```kotlin
rude manner ask(Manifest.permission.ACCESS_COARSE_LOCATION)
```

### Невозможность запросить разрешение

- Открывает дополнительную разметку, предлагающую перейти в настройки

```kotlin
suggest goTo innerChamber
```

## Внешний вид

Для кастомизации интерфейса необходимо переопределить класс `GentlemanAppearance` -
подготовительный этап, который далее передается в `Appearance`

```kotlin
class Tuxedo(
    override val rationaleLayoutResId: Int = R.layout.tuxedo,
    override val settingLayoutResId: Int = R.layout.inner_chamber,
    override val appearanceClass: Class<out Appearance> = Appearance::class.java,
    reparation: Preparation,
) : GentlemanAppearance(reparation)
```

`Appearance` - это полноценная activity в которой вы вольны делать что угодно. Необходимо только
использовать специальные id (**button_positive** и **button_negative**) для view для обработки
нажатий соответствующих кнопок

Иногда достаточно будет переопределить только саму разметку `R.layout.tuxedo`
