(ns re-frame-form.events
  (:require [re-frame.core :refer [dispatch
                                   reg-event-db
                                   reg-fx]]))

(def default-errors [])
(def default-transformers [])
(def default-validators [])

(defn register-events
  [{:keys [init-field-interceptors
           clear-interceptors
           update-field-error-interceptors
           update-field-value-interceptors
           form-interceptors]
    :or {init-field-interceptors []
         clear-interceptors []
         update-field-error-interceptors []
         update-field-value-interceptors []
         form-interceptors []}}]

  (reg-event-db
   :form/init-field
   (into form-interceptors init-field-interceptors)
   (fn [db [_ {:keys [:form/id
                     :field/default-value
                     :field/errors
                     :field/key
                     :field/transformers
                     :field/validators]
              :or {errors default-errors
                   transformers default-transformers
                   validators default-validators}}]]
     (-> db
         (assoc-in [:form id :data key] default-value)
         (assoc-in [:form id :errors key] errors)
         (assoc-in [:form id :validators key] validators)
         (assoc-in [:form id :transformers key] transformers))))

  (reg-event-db
   :form/update-field-value
   (into form-interceptors update-field-value-interceptors)
   (fn [db [_ {:keys [:form/id
                     :field/key
                     :field/value]}]]
     (-> db
         (assoc-in [:form id :data key] value)
         (assoc-in [:form id :errors key] default-errors))))

  (reg-event-db
   :form/update-field-error
   (into form-interceptors update-field-error-interceptors)
   (fn [db [_ {:keys [:form/id
                     :field/key
                     :field/error]}]]
     (assoc-in db [:form id :errors key] error)))

  (reg-event-db
   :form/clear
   (into form-interceptors clear-interceptors)
   (fn [db [_ id]]
     (update db :form dissoc id)))

  (reg-fx :clear-form #(dispatch [:form/clear %])))
