(ns re-frame-form.validators)

(defn required [v] (not (empty? v)))
