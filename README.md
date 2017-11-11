# re-frame-form

A ClojureScript (re-frame / reagent) library for working with forms

## Install

[![Clojars Project](https://img.shields.io/clojars/v/oconn/re-frame-form.svg)](https://clojars.org/oconn/re-frame-form)

## Usage

### Sample Form

```cljs
(ns app.core
  (:require [:re-frame-form.core :refer [form input]]
            [:re-frame-form.validators :as v]
            [:re-frame-form.transformers :as t])

(defn sample-form
  []
  [form {:id :form-sample}
   [:form {:rff/form {:on-submit #(prn %)}}
    (input {:type "text"
            :key :first-name
            :label "First Name"
            :validators [{:validator v/required
                          :message "Required"}]
            :transformers [t/trim]})

    (input {:type "text"
            :key :last-name
            :label "Last Name"
            :validators [{:validator v/required
                          :message "Required"}]
            :transformers [t/trim]})

    (input {:type "text"
            :key :email
            :label "Email"
            :validators [{:validator v/required
                          :message "Required"}
                         {:validator v/simple-email
                          :message "Invalid Email Address"}]
            :transformers [t/trim
                           t/ensure-lower]
            :masks [t/trim]})

    [:input {:rff/submit-button true
             :type :submit}]]])
```

### TODO

## License

Copyright © 2017 Matt O'Connell

Distributed under the Eclipse Public License.
