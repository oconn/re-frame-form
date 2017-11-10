(ns re-frame-form.subscriptions
  (:require [re-frame.core :refer [reg-sub
                                   subscribe]]))

(defn register-subscriptions
  []
  (reg-sub :form (fn [{:keys [form]}] form))

  (reg-sub
   :form/subscribe-to
   (fn [_ _] (subscribe [:form]))
   (fn [forms [_ {:keys [:form/id]}]]
     (get forms id)))

  (reg-sub
   :form/subscribe-to-input
   (fn [_ _] (subscribe [:form]))
   (fn [forms [_ {:keys [:form/id :field/key]}]]
     {:value (get-in forms [id :data key])
      :errors (get-in forms [id :errors key])})))
