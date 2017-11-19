(ns re-frame-form.components.button
  (:require [re-frame.core :as re-frame]

            [re-frame-form.components.utils :as u]))

(defn mount-submit-button
  [node form-id is-submitting]
  (let [params (second node)

        mounted-node (-> node
                      (update-in [1] dissoc :rff/submit-button)
                      (u/add-class "rff-submit-button"))
        form-data (re-frame/subscribe [:form/form form-id])]
    (fn []
      (let [errors (u/get-form-errors (:errors @form-data))
            is-dispatching (and (not (nil? is-submitting)) @is-submitting)]
        (cond
          (not-empty errors)
          (assoc-in mounted-node [1 :disabled] true)

          is-dispatching
          (-> mounted-node
              (assoc-in [1 :disabled] true)
              (u/add-class "rff-is-loading"))

          :else
          mounted-node)))))
