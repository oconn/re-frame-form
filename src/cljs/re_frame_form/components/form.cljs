(ns re-frame-form.components.form
  (:require [re-frame.core :as re-frame]

            [re-frame-form.components.utils :as u]))

(defn- apply-transformers
  [{:keys [transformers] :as form}]
  (update form :data
          #(apply merge
                  (map (fn [[field-name value]]
                         (let [trxs (get transformers field-name [])]
                           {field-name ((apply comp trxs) value)}))
                       %))))

(defn- form-submit-fn
  [form-id
   {:keys [on-submit clear-on-submit]
    :or {clear-on-submit false}}]
  (let [form-data (re-frame/subscribe [:form/form form-id])]
    (fn [e]
      (.preventDefault e)

      (let [errors (u/validate-form! @form-data form-id)]
        (when (empty? (filter not-empty errors))

          (when clear-on-submit
            (re-frame/dispatch [:form/clear-form form-id]))

          (on-submit (-> @form-data
                         apply-transformers
                         :data)))))))

(defn mount-form
  [node form-id]
  (let [params (second node)

        mounted-node
        (assoc-in node [1]
                  (-> params
                      (dissoc :rff)
                      (assoc :on-submit (form-submit-fn form-id
                                                        (:rff/form params)))))]
    (fn []
      mounted-node)))
