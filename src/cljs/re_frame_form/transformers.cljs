(ns re-frame-form.transformers
  (:require [cognitect.transit :as t]))

(def ensure-lower
  "Forces lower case"
  clojure.string/lower-case)
(def trim
  "Trims whitespace"
  clojure.string/trim)

(defn alpha-only
  "Only allow non digit values"
  [value]
  (clojure.string/replace value #"\d" ""))

(defn digit-only
  "Only allow digits"
  [value]
  (clojure.string/replace value #"\D" ""))

(defn float-only
  "Only allow digits and decimal point"
  [value]
  (clojure.string/replace value #"[^0-9\.]" ""))

(defn no-whitespace
  "Disallow whitespace"
  [value]
  (clojure.string/replace value #"\s" ""))

(defn str->int
  "Converts a string to an integer"
  [value]
  (if (empty? value)
    nil
    (js/parseInt value)))

(defn str->bigdec
  "Converts a string to bigdec (transit tagged value)"
  [value]
  (t/bigdec value))
