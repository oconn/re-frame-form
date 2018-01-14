(ns re-frame-form.components.utils
  (:require [re-frame.core :as re-frame]))

(defn get-event-value
  [e]
  (let [target (-> e .-target)
        type (.-type target)]
    (case type
      "radio" (.-id target)
      "checkbox" (.-checked target)
      (.-value target))))

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
  (fn [e]
    (re-frame/dispatch
     [:form/update-field-value
      form-id
      {:field/key key
       :field/value ((apply comp masks) (get-event-value e))}])))

(defn input-blur-fn
  [form-id
   {:keys [key validators]}]
  (fn [e]
    (validate-field! (get-event-value e)
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
