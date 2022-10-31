# RapidX [![](https://jitpack.io/v/fiftyonemoon/RapidX.svg)](https://jitpack.io/#fiftyonemoon/RapidX)
 A powerful android library which can be make an app development easy.
 
## What's New
- Introducing Auto Scale Views.
   - Now XML view can scale automatically without providing width and height.
   - Refer [`AutoRapidConstraintLayout`](app/src/main/res/layout/rapid_constraint_layout_auto.xml).

## Contains
- Providers (Media, Files, Directory, Dialogs).
- Views (Hybrid RapidViews based on Originals).
- UI (Global Methods).

## Implementation

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

```groovy
//Reduce an app size by excluding unused modules from library.

implementation('com.github.fiftyonemoon:RapidX:1.0.0') {
    //exclude module: 'provider' (for extarnal use)
    //exclude module: 'views' (for scaling)
    //exclude module: 'ui' (for common use)
}
```
## Scale Views
- Create your design with default 1080x1920 screen size.
- For better experience hide navigation and status/notification bar.
