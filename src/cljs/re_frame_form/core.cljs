(ns re-frame-form.core
  (:require [cljs.spec.alpha :as s]

            [re-frame-form.components.core :as rff-components]
            [re-frame-form.events :as rf-form-events]
            [re-frame-form.subscriptions :as rf-form-subscriptions]))

(comment
  "Inspiration came from the existing re-frame form lib free-form

  https://github.com/pupeno/free-form/blob/master/src/cljs/free_form/core.cljs")

(def initial-state
  "Initial state for re-frame-form"
  {})

(s/def ::data map?)

(s/def ::errors
  (s/map-of keyword? (s/coll-of string?)))

(s/def :validator/validator fn?)
(s/def :validator/message string?)
(s/def ::validators
  (s/map-of keyword? (s/coll-of (s/keys :req-un [:validator/validator
                                                 :validator/message]))))

(s/def ::transformers
  (s/map-of keyword? (s/coll-of fn?)))

(s/def ::form (s/map-of keyword? (s/keys :req-un [::data
                                                  ::errors
                                                  ::validators
                                                  ::transformers])))

(defn register-events
  "Register re-frame-form events"
  [opts]
  (rf-form-events/register-events opts))

(defn register-subscriptions
  "Registers re-frame-form subscriptions"
  []
  (rf-form-subscriptions/register-subscriptions))

(defn register-all
  "Registers both re-frame-form events & subscriptions"
  [{:keys [event-options]}]
  (register-events event-options)
  (register-subscriptions))

(def form rff-components/form)
