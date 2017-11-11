(ns re-frame-form.components.button
  (:require [re-frame.core :as re-frame]

            [re-frame-form.components.utils :as u]))

(defn mount-submit-button
  [node form-id]
  (let [params (second node)

        mounted-node (->> (update-in node [1] dissoc :rff/submit-button)
                          (u/add-class "submit-button"))
        form-data (re-frame/subscribe [:form/form form-id])]
    (fn []
      (let [errors (u/get-form-errors (:errors @form-data))]
        (cond
          (not-empty errors)
          (assoc-in mounted-node [1 :disabled] true)

          :else
          mounted-node)))))
