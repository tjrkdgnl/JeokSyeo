## :beer: 적셔 앱이란?
- 적셔는 세상의 모든 주류를 하나의 앱에서 검색을 통해 손 쉽게 찾을 수 있는 백과사전 서비스를 제공하며 구독 서비스를 통해 매달 전통주를 집으로 받아볼 수 있는 이커머스 형 앱입니다.


## :pushpin: Feature
1. 소셜 로그인
2. 주류 찜하기
3. 주류 검색하기
4. 주류 평가하기
5. 주류 좋아요 및 싫어요
6. 나의 주류 레벨
7. 내가 찜한 주류 목록
8. 내가 평가한 주류 목록

![적셔](https://user-images.githubusercontent.com/45396949/133727508-60109237-a93c-43db-bc4e-7ac03daef620.gif)


## :books: Spec
- Language: Kotlin
- Minimum SDK level: 19
- Maximum SDK level: 29
- Kotlin base with [RxKotlin2,RxAndroid](https://github.com/ReactiveX/RxKotlin) for Asynchronous task
- Android Jetpack
  - LiveData
  - Lifecycle
  - ViewModel
  - DataBinding
- Library
  - [Retrofit2](https://github.com/square/retrofit)
  - [Glide](https://github.com/bumptech/glide)
  - [Lottie](https://github.com/airbnb/lottie-android)
  - [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) 
  - [Okhttp3](https://github.com/square/okhttp)
  - Firebase
    - auth
    - deepLink
    - analytics
    - crashlytics  
  - MultiDex
  - KaKao Login
  - Naver Login
  - Google Login
  - Meterial Design
  - expandablelayout
  - expandableTextView
  - TedPermission
  - ucrop
  - ratingbar
    - SimpleRatingbar
    - BubbleSeekbar
  - [Balloon](https://github.com/skydoves/Balloon)
  - [ScalableLayout](https://github.com/ssomai/ScalableLayout)
    
## Architecture
- MVP Design Pattern(Model - View - Presenter - DataBinding)

![mvp](https://user-images.githubusercontent.com/45396949/133721864-5139da96-d339-4869-911f-30f076af5af5.PNG)

## 배운 것
- 전반적인 Android Framework에 대해 이해할 수 있었으며, Activity, Fragment lifeCycle에 따라 UI 처리 방법에 대해 배울 수 있었다.

- 안드로이드는 Single UI Thread만 사용하기 때문에 네트워크 통신 및 무거운 작업은 Worker Thread를 통해 처리해야 하는 것을 알게 되었다.
- MVP 패턴을 적용하면서 Interface 활용법에 대해서 더 자세하게 배울 수 있었으며 콜백 작성 방법에 대해서 배울 수 있었다. 또한 앱 전반적인 구조를 명확하게 구분지어 더 편안한 유지보수 환경을 만들 수 있었다.
- Retrofit과 RxJava를 사용하여 비동기 통신 방법에 대해 배울 수 있었다.
- DataBinding을 사용하여 보일러플레이트한 코드를 줄이고 NullPointerException 등의 에러가 발생할 수 있는 확률을 줄임으로써 앱 성능을 개선시킬 수 있었다.
- Fragment에서 databinding을 사용하면 onDestoryView 이후에도 binding 객체가 게속 view를 참조하고 있기 때문에 memory leak이 발생할 수 있다. 따라서 반드시 onDestroyView에서 명시적으로 binding 객체를 null 처리 해야하는 것을 알게 되었다.
- 소셜 로그인을 위해 네이버, 카카오톡, 구글, Apple Login API를 사용해 볼 수 있었다.
- Token parsing을 통해 Login 정보 및 사용자 정보를 얻고 사용할 수 있는 경험을 할 수 있었다. 
- Firebase에서 제공하는 기능들을 사용해볼 수 있었으며 특히 crashlytics 통해 유저에게 발생한 에러를 확인하고 조치할 수 있는 경험을 할 수 있었다. 또한 deepLink를 통해 QR 스캔 시, 정해놓은 로직을 수행할 수 있도록 만들 수 있었다. 
- ConstraintLayout과 scalableLayout 라이브러리를 함께 사용하여 반응형 레이아웃을 만들 수 있었다.
- Glide 라이브러리를 사용하여 url image를 뷰에 셋팅한 경험이 있다. 이 과정에서 Glide는 httpUrlConnection를 커스텀한 라이브러리라는 것을 알게 되었으며, 이를 통해 url image를 얻기 위한 별다른 처리를 하지 않아도 이미지를 셋팅할 수 있다는 것을 이해할 수 있었다. 또한 Glide의 이미지가 변경되지 않던 이슈가 발생했었는데 이는 Glide 내부적으로 캐시 기능때문이란 것을 알게 되었다. 
