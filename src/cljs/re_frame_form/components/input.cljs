(ns re-frame-form.components.input
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]

            [re-frame-form.components.utils :as u])
  (:import [goog.async Debouncer]))

(defn debounce
  {:attribution "https://www.martinklepsch.org/posts/simple-debouncing-in-clojurescript.html"}
  [f interval]
  (let [dbnc (Debouncer. f interval)]
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))

(defn update-input!
  [element value]
  (set! (.-value element) value))

(defn mount-input
  [node form-id is-submitting]
  (let [!ref
        (atom nil)

        params
        (second node)

        {:keys [key
                validators
                transformers
                default-value
                on-change
                on-blur
                controlled
                strict-controlled
                placeholder]
         :or {default-value ""
              validators []
              transformers []
              on-change identity
              on-blur identity
              controlled true
              strict-controlled false}
         :as rff-params}
        (:rff/input params)

        input-data
        (re-frame/subscribe [:form/field form-id key])

        re-frame-form-on-change
        (if (and controlled (not strict-controlled))
          (debounce (u/input-change-fn form-id rff-params) 150)
          (u/input-change-fn form-id rff-params))

        re-frame-form-on-blur
        (u/input-blur-fn form-id rff-params)

        mounted-node
        (assoc-in node [1]
                  (-> params
                      (dissoc :rff/input)
                      (merge {:ref #(reset! !ref %)
                              :on-change #(do
                                            (when controlled
                                              (update-input! @!ref
                                                             (-> %
                                                                 .-target
                                                                 .-value)))

                                            (on-change %)
                                            (re-frame-form-on-change !ref))
                              :on-blur #(do
                                          (on-blur %)
                                          (re-frame-form-on-blur !ref))})))]

    (u/initialize-field {:key key
                         :validators validators
                         :transformers transformers
                         :default-value default-value
                         :form-id form-id})
    (reagent/create-class
     {:component-did-mount
      (fn []
        (update-input! @!ref default-value))
      :reagent-render
      (fn []
        (let [{value :value errors :errors} @input-data

              disabled (when (not (nil? is-submitting))
                         @is-submitting)

              node-with-value (cond-> mounted-node
                                disabled
                                (assoc-in [1 :disabled] disabled)

                                (not (nil? placeholder))
                                (assoc-in [1 :placeholder] placeholder))]

          (when (and controlled (not (nil? @!ref)))
            (update-input! @!ref value))

          (if (not-empty errors)
            (u/add-class node-with-value "error")
            node-with-value)))})))
