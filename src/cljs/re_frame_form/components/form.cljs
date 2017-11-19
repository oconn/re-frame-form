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
    :or {clear-on-submit false}}
   is-submitting]
  (let [form-data (re-frame/subscribe [:form/form form-id])]
    (fn [e]
      (.preventDefault e)

      (let [errors (u/validate-form! @form-data form-id)
            no-errors (empty? (filter not-empty errors))
            is-submitting-value (and (not (nil? is-submitting))
                                     (= true @is-submitting))]

        (when (and no-errors (not is-submitting-value))

          (when clear-on-submit
            (re-frame/dispatch [:form/clear-form form-id]))

          (on-submit (-> @form-data
                         apply-transformers
                         :data)))))))

(defn mount-form
  [node form-id is-submitting]
  (let [params (second node)

        mounted-node
        (assoc-in node [1]
                  (-> params
                      (dissoc :rff/form)
                      (assoc :on-submit (form-submit-fn form-id
                                                        (:rff/form params)
                                                        is-submitting))))]
    (fn []
      mounted-node)))
