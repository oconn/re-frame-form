(ns re-frame-form.components.core
  (:require [clojure.walk :refer [postwalk]]
            [re-frame.core :as re-frame]

            [re-frame-form.components.button :as button]
            [re-frame-form.components.form :as form]
            [re-frame-form.components.input :as input]
            [re-frame-form.components.field-error :as field-error]
            [re-frame-form.components.select :as select]))

(defn- rff-node?
  [node key]
  (and (coll? node)
       (contains? (second node) key)))

(defn- walk-node
  ([form-data]
   (walk-node form-data []))
  ([{:keys [id is-submitting]} custom-nodes]
   (when (and (not (nil? is-submitting))
              (not= (type is-submitting)
                    reagent.ratom/Reaction))
     (throw
      (js/Error. "When passing \"is-submitting\" into re-frame-from, you must pass a reagent ratom")))

   (fn [node]
     (cond
       (rff-node? node :rff/form)
       [form/mount-form node id is-submitting]

       (rff-node? node :rff/input)
       [input/mount-input node id is-submitting]

       (rff-node? node :rff/select)
       [select/mount-select node id is-submitting]

       (rff-node? node :rff/submit-button)
       [button/mount-submit-button node id is-submitting]

       (rff-node? node :rff/field-error)
       [field-error/mount-field-error node id]

       ;; TODO Check custom nodes

       :else node))))

(defn form
  [form-data html]
  [postwalk (walk-node form-data) html])

(defn input
  [{:keys [type
           key
           label
           validators
           transformers
           masks
           default-value
           on-change
           controlled
           strict-controlled
           placeholder]
    :or {type :text
         validators []
         transformers []
         masks []
         default-value ""
         on-change identity
         controlled true
         strict-controlled false
         placeholder nil}}]
  [:div.rff-input-wrapper
   [:label.rff-input-label {:for key} label]
   [:input.rff-input {:rff/input {:key key
                                  :validators validators
                                  :transformers transformers
                                  :on-change on-change
                                  :masks masks
                                  :default-value default-value
                                  :controlled controlled
                                  :strict-controlled strict-controlled
                                  :placeholder placeholder}
                      :id key
                      :type type}]
   [:p.rff-field-error {:rff/field-error {:key key}}]])

(defn select
  [{:keys [key label validators default-value options on-change]
    :or {options [{:value ""
                   :display "Select an option"
                   :disabled true}]
         on-change identity}}]
  [:div.rff-input-wrapper
   [:label.rff-input-label {:for key} label]
   [:select.rff-select {:rff/select {:key key
                                     :validators validators
                                     :options options
                                     :on-change on-change
                                     :default-value (or default-value
                                                        (:value (first options)))}
                        :id key}]
   [:p.rff-field-error {:rff/field-error {:key key}}]])
