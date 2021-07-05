(ns hexagram-svg-generator.frontend
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as r.dom]
   [hexagram-svg-generator.hex-gen :as hex-gen :refer [hexagram-svg]]))

(defonce hexagram-number
  (atom "1"))

(defonce last-valid-number
  (atom "1"))

(defn valid? [num]
  (and (<= 1 num)
       (>= 64 num)
       (not (nil? num))))

(defn hexagram-info
  [hexagram-number]
  [:div
   [:h2
    (str "Hexagram #" hexagram-number )
    [:br]
    (str "-- " (hex-gen/hexagram-name hexagram-number) " --")]
   (hexagram-svg hexagram-number)
   [:h3 (str "Binary Representation: " (hex-gen/hexagram-bin hexagram-number))]])

(defn ui []
  [:div
   [:h1 "I-ching Hexagram Generator"]
   (if (valid? @hexagram-number)
     (do (reset! last-valid-number @hexagram-number)
         (hexagram-info @hexagram-number))
     (hexagram-info @last-valid-number))
   [:p "Hexagram number (1-64): "
    [:input {:type "text"
             :value @hexagram-number
             :on-change #(reset! hexagram-number (-> % .-target .-value))}]
    (if (valid? @hexagram-number)
      nil
      [:p "Put in a number from 1-64."])]
   [:button {:on-click #(reset! hexagram-number (inc (rand-int 64)))} "Generate New Hexagram"]])




(defn start []
  (r.dom/render [ui]
                (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
