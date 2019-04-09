# Toggl Reporting Sample App

This is a demonstration app just to test out some of the newest features around
Spring and Kotlin. The application itself uses the [Toggl Api](https://github.com/toggl/toggl_api_docs) to access
a Toggl Project and generate reports. I use this to generate for myself an excel.

Feel free to copy the work around, mentions are welcome. Be aware that I normally only use the software for myself, so 
if you find any problems, please open an issue on [github](https://github.com/titaniumcoder/toggl-reporting) and I will see to fix it.

## Features

- [Kotlin](https://kotlinlang.org/) and there especially:
  - Custom Typesafe DSL for [Apache Poi](https://poi.apache.org/index.html) Excel Files. Find documentation on this
    Kotlin Feature here: https://kotlinlang.org/docs/reference/type-safe-builders.html
  - [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for handling the fetching of the data via Toggl Api above.
- Deployment to [Heroku](https://heroku.com) - one button deployment
- CI/CD with [Travis](https://travis-ci.com) 
- Testing with [Spek](https://spekframework.org/)

---
Copyright 2019 Rico Metzger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
