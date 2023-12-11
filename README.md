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

В Activity/Fragment/View описываем необходимые разрешения и обработчик результата

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

Есть возможность перезапросить разрешение, если в первый раз получили отказ

```kotlin
ask(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.POST_NOTIFICATIONS
) retry once
```

Не забываем, что после Adnroid 11 разрешения не запрашиваются, если были отклонены дважды

## Внешний вид

Для кастомизации Rationale интерфейса необходимо переопределить класс `GentlemanAppearance` соблюдая
аргументы

```kotlin
class Tuxedo(
    override val layoutResId: Int = R.layout.tuxedo,
    reparation: Preparation? = null
) : GentlemanAppearance(reparation)
```

Это полноценная activity в которой вы вольны делать что угодно. Необходимо только использовать
специальные id для view (**button_positive** и **button_negative**) для обработки нажатий
соответствующих кнопок

Иногда достаточно будет переопределить только саму разметку `R.layout.tuxedo`
