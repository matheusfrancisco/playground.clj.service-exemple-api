(ns template-clj.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [template-clj.protocols.storage-client :as storage-client]))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!!" :status 200}))

(defn get-all-users! [storage]
  (storage-client/read-all storage))

(defn get-all-users
  [{{storage :storage} :components}]
  (ring-resp/response {:message (get-all-users! storage)}))

(def common-interceptors
  [(body-params/body-params) http/json-body])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/users" :get (conj common-interceptors `get-all-users)]})
