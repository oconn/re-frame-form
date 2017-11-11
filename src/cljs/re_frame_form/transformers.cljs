(ns re-frame-form.transformers)

(def ensure-lower clojure.string/lower-case)
(def trim clojure.string/trim)

(defn alpha-only
  [value]
  (clojure.string/replace value #"\d" ""))

(defn digit-only
  [value]
  (clojure.string/replace value #"\D" ""))

(defn no-whitespace
  [value]
  (clojure.string/replace value #"\s" ""))
