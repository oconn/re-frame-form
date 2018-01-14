(ns re-frame-form.validators)

(defn required [v]
  (cond
    (keyword? v) true

    :else (not (empty? v))))

(defn simple-email
  [v]
  (re-matches #"^[a-zA-Z0-9._+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$" v))
