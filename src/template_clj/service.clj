(ns template-clj.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [template-clj.protocols.storage-client :as storage-client]))

(defn- sucess-response
  [result status]
   (-> result
       ring-resp/response
       (ring-resp/status status)))

(defn- bad-response
  ([errors]
   (bad-response errors 500))
  ([errors status]
   (let [st (if (nil? status) 500 status)]
     (-> {:errors errors}
         ring-resp/response
         (ring-resp/status st)))))

(defn- handle-response-create!
  [response]
  (let [object response]
    (if false
      (bad-response "error" 404)
        (sucess-response {:message "create" :db object} 201))))

;(defn- handle-response!
;  [response]
;  (let [{result :result errors :errors status :status} response]
;    (prn response)
;    (if (some? errors)
;      (bad-response errors status)
;        (sucess-response result status))))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!!" :status 200}))

(defn get-all-users! [storage]
  (storage-client/read-all storage))


(defn get-database
  [{{storage :storage} :components}]
  storage)

(defn get-all-users
  [request]
  (let [storage (get-database request)]
    (ring-resp/response {:message (get-all-users! storage)})))

(defn validate-user
  [user]
  user)

(defn create-user!
  [user db]
  (storage-client/put! db #(%1 %2 conj user)))

(defn handler-create-user
  [request]
  (let [storage (get-database request)
        new-user (:json-params request)]
    (handle-response-create! (create-user! new-user storage))))

(def common-interceptors
  [(body-params/body-params) http/json-body])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/users" :get (conj common-interceptors `get-all-users)]
    ["/users" :post (conj common-interceptors `handler-create-user)]})
