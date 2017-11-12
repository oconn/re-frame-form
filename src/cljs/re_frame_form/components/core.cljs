(ns re-frame-form.components.core
  (:require [clojure.walk :refer [postwalk]]
            [re-frame.core :as re-frame]

            [re-frame-form.components.button :as button]
            [re-frame-form.components.form :as form]
            [re-frame-form.components.input :as input]
            [re-frame-form.components.field-error :as field-error]))

(defn- rff-node?
  [node key]
  (and (coll? node)
       (contains? (second node) key)))

(defn- walk-node
  ([form-data]
   (walk-node form-data []))
  ([{:keys [id]} custom-nodes]
   (fn [node]
     (cond
       (rff-node? node :rff/form)
       [form/mount-form node id]

       (rff-node? node :rff/input)
       [input/mount-input node id]

       (rff-node? node :rff/submit-button)
       [button/mount-submit-button node id]

       (rff-node? node :rff/field-error)
       [field-error/mount-field-error node id]

       ;; TODO Check custom nodes

       :else node))))

(defn form
  [form-data html]
  [postwalk (walk-node form-data) html])

(defn input
  [{:keys [type key label validators transformers masks]
    :or {type :text
         validators []
         transformers []
         masks []}}]
  [:div.rff-input
   [:label.rff-input-label {:for key} label]
   [:input.rff-input {:rff/input {:key key
                                  :validators validators
                                  :transformers transformers
                                  :masks masks}
                      :id key
                      :type type}]
   [:p.rff-field-error {:rff/field-error {:key key}}]])
