(ns re-frame-form.components.select
  (:require [re-frame.core :as re-frame]

            [re-frame-form.components.utils :as u]))

(defn mount-select
  [node form-id is-submitting]
  (let [!ref
        (atom nil)

        params
        (second node)

        {:keys [key
                validators
                transformers
                options
                default-value
                placeholder
                on-change
                on-blur]
         :or {validators []
              transformers []
              on-change identity
              on-blur identity}
         :as rff-params}
        (:rff/select params)

        select-data
        (re-frame/subscribe [:form/field form-id key])

        re-frame-form-on-change
        (u/input-change-fn form-id rff-params)

        re-frame-form-on-blur
        (u/input-blur-fn form-id rff-params)

        formatted-params
        (-> params
            (dissoc :rff/select)
            (merge {:ref #(reset! !ref %)
                    :on-change #(do
                                 (on-change %)
                                 (re-frame-form-on-change !ref))
                    :on-blur #(do
                                (on-blur %)
                                (re-frame-form-on-blur !ref))}))

        mounted-node
        (-> node
            (assoc-in [1] formatted-params)
            (conj (for [{:keys [value display disabled]
                         :or {disabled false}}
                        options]
                    ^{:key value} [:option
                                   {:value value
                                    :disabled disabled}
                                   display])))]

    (u/initialize-field {:key key
                         :validators validators
                         :transformers transformers
                         :default-value default-value
                         :form-id form-id})
    (fn []
      (let [is-loading (= (:value @select-data) nil)]
        (if is-loading
          ;; TODO (Is there a better default for this?)
          ;;
          ;; React throws warnings when the value is nil and on first render
          ;; the value will be nil  until the component has had a tick to
          ;; complete it's initialization
          [:select {:value ""}
           [:option {:value ""} "Loading options..."]]
          (let [{value :value
                 errors :errors} @select-data
                disabled (when (not (nil? is-submitting))
                           @is-submitting)]
            (let [node-with-value (-> mounted-node
                                      (assoc-in [1 :value] value)
                                      (assoc-in [1 :disabled] disabled))]
              (if (not-empty errors)
                (u/add-class node-with-value "error")
                node-with-value))))))))
