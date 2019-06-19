# Toggl Reporting Sample App

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
[![Build Status](https://travis-ci.com/titaniumcoder/toggl-reporting.svg?branch=master)](https://travis-ci.com/titaniumcoder/toggl-reporting)
[![codecov](https://codecov.io/gh/titaniumcoder/toggl-reporting/branch/master/graph/badge.svg)](https://codecov.io/gh/titaniumcoder/toggl-reporting)

This is a demonstration app just to test out some of the newest features around
Spring and Kotlin. The application itself uses the [Toggl Api](https://github.com/toggl/toggl_api_docs) to access
a Toggl Project and generate reports. I use this to generate for myself an excel.

Feel free to copy the work around, mentions are welcome. Be aware that I normally only use the software for myself, so 
if you find any problems, please open an issue on [github](https://github.com/titaniumcoder/toggl-reporting) and I will see to fix it.

#### Frontend
The front-end is more a "sample" than a real-world application. It's one html page together with a javascript
file, no workflow, no ci-cd, code-style quite bad (disadvantage of using plain javascript). It's more kind of a demonstration
how to call the API than something that should be used like this. It uses Vue, has no UX and really nothing in direction
of tests. I normally would say **never do it like that** &smile;.

## Features

- [Kotlin](https://kotlinlang.org/) and there especially:
  - Custom Typesafe DSL for [Apache Poi](https://poi.apache.org/index.html) Excel Files. Find documentation on this
    Kotlin Feature here: https://kotlinlang.org/docs/reference/type-safe-builders.html
  - [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for handling the fetching of the data via Toggl Api above.
- Deployment to [Heroku](https://heroku.com) - one button deployment
- CI/CD with [Travis](https://travis-ci.com) 

---
Copyright 2019 Rico Metzger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
