(ns re-frame-form.components.field-error
  (:require [re-frame.core :as re-frame]))

(defn mount-field-error
  [node form-id]
  (let [key (get-in node [1 :rff/field-error :key])
        field-data (re-frame/subscribe [:form/field form-id key])]
    (fn []
      (let [{errors :errors} @field-data]
        (when (not-empty errors)
          [assoc-in node [1] (first errors)])))))
