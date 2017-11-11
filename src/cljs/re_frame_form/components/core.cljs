(ns re-frame-form.components.core
  (:require [clojure.walk :refer [postwalk]]
            [re-frame.core :as re-frame]

            [re-frame-form.components.button :as button]
            [re-frame-form.components.form :as form]
            [re-frame-form.components.input :as input]))

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

       ;; (form-node? node :rff/input-label)
       ;; [modify-input-label node form-name]

       ;; (form-node? node :rff/input-error)
       ;; [modify-input-error node form-name]

       ;; (form-node? node :rff/input-hint)
       ;; [modify-input-hint node form-name]

       ;; TODO Check custom nodes

       :else node))))

(defn form
  [form-data html]
  [postwalk (walk-node form-data) html])
