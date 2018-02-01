(ns re-frame-form.components.utils
  (:require [re-frame.core :as re-frame]))

(defn get-dom-node-value
  [node]
  (case (.-type node)
    "radio" (.-id node)
    "checkbox" (.-checked node)
    (.-value node)))

(defn get-event-value
  [e]
  (get-dom-node-value (-> e .-target)))

(defn add-class
  [node class-name]
  (update-in node [0]
             #(keyword (str (name %) "." class-name))))

(defn get-form-errors
  [errors]
  (flatten
   (map (fn [[_ field-errors]]
          field-errors) errors)))

(defn get-input-errors
  [validators value]
  (reduce (fn [errors {:keys [validator message]}]
            (let [is-valid (validator value)]
              (if is-valid
                errors
                (conj errors message))))
          [] validators))

(defn validate-field!
  [value validators key form-id]
  (let [errors (get-input-errors validators value)]
    (re-frame/dispatch [:form/update-field-errors
                        form-id
                        {:field/key key
                         :field/errors errors}])
    errors))

(defn validate-form!
  [{:keys [data errors validators]} form-id]
  (doall
   (map (fn [[field-key value]]
          (let [field-validators (get validators field-key)]
            (validate-field! value
                             field-validators
                             field-key
                             form-id)))
        data)))

(defn input-change-fn
  [form-id
   {:keys [key masks]}]
  (fn [!ref]
    (re-frame/dispatch
     [:form/update-field-value
      form-id
      {:field/key key
       :field/value ((apply comp masks) (get-dom-node-value @!ref))}])))

(defn input-blur-fn
  [form-id
   {:keys [key validators]}]
  (fn [!ref]
    (validate-field! (get-dom-node-value @!ref)
                     validators
                     key
                     form-id)))

(defn initialize-field
  [{:keys [key default-value validators transformers form-id]}]
  (re-frame/dispatch [:form/initialize-field
                      form-id
                      {:field/key key
                       :field/default-value default-value
                       :field/validators validators
                       :field/transformers transformers}]))
