(ns re-frame-form.components.input
  (:require [re-frame.core :as re-frame]

            [re-frame-form.components.utils :as u]))

(defn mount-input
  [node form-id is-submitting]
  (let [params
        (second node)

        {:keys [key
                validators
                transformers
                default-value
                on-change
                on-blur]
         :or {default-value ""
              validators []
              transformers []
              on-change identity
              on-blur identity}
         :as rff-params}
        (:rff/input params)

        input-data
        (re-frame/subscribe [:form/field form-id key])

        mounted-node
        (assoc-in node [1]
                  (-> params
                      (dissoc :rff/input)
                      (merge {:on-change (u/input-change-fn form-id
                                                            rff-params
                                                            on-change)
                              :on-blur (u/input-blur-fn form-id
                                                        rff-params
                                                        on-blur)})))]

    (u/initialize-field {:key key
                         :validators validators
                         :transformers transformers
                         :default-value default-value
                         :form-id form-id})

    (fn []
      (let [{value :value
             errors :errors} @input-data
            disabled (when (not (nil? is-submitting))
                       @is-submitting)]
        (let [node-with-value (-> mounted-node
                                  (assoc-in [1 :value] value)
                                  (assoc-in [1 :disabled] disabled))]
          (if (not-empty errors)
            (u/add-class node-with-value "error")
            node-with-value))))))
