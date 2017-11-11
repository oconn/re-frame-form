(ns re-frame-form.components.input
  (:require [re-frame.core :as re-frame]

            [re-frame-form.components.utils :as u]))

(defn- input-change-fn
  [form-id
   {:keys [key masks]}]
  (fn [e]
    (re-frame/dispatch
     [:form/update-field-value
      form-id
      {:field/key key
       :field/value ((apply comp masks) (u/get-event-value e))}])))

(defn- input-blur-fn
  [form-id
   {:keys [key validators]}]
  (fn [e]
    (u/validate-field! (u/get-event-value e)
                       validators
                       key
                       form-id)))

(defn- mount-input
  [node form-id]
  (let [params
        (second node)

        {:keys [key validators transformers default-value]
         :or {default-value ""
              validators []
              transformers []}
         :as rff-params}
        (:rff/input params)

        input-data
        (re-frame/subscribe [:form/field form-id key])

        mounted-node
        (assoc-in node [1]
                  (-> params
                      (dissoc :rff/input)
                      (merge {:on-change (input-change-fn form-id rff-params)
                              :on-blur (input-blur-fn form-id rff-params)})))]

    (re-frame/dispatch [:form/initialize-field
                        form-id
                        {:field/key key
                         :field/default-value default-value
                         :field/validators validators
                         :field/transformers transformers}])
    (fn []
      (let [{value :value
             errors :errors} @input-data]
        (let [node-with-value (assoc-in mounted-node [1 :value] value)]
          (if (not-empty errors)
            (u/add-class "error" node-with-value)
            node-with-value))))))
