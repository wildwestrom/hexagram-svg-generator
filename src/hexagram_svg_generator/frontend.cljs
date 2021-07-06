(ns hexagram-svg-generator.frontend
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as r.dom]
   [hexagram-svg-generator.hex-gen :as hex-gen :refer [hexagram-svg]]))

(defonce hexagram-number
  (atom "1"))

(defonce last-valid-number
  (atom "1"))

(defonce scale-factor
  (atom "7"))

(defn valid? [num]
  (and (<= 1 num)
       (>= 64 num)
       (not (nil? num))))

(defn hexagram-info
  [hexagram-number]
  [:div
   [:h2
    (str "Hexagram #" hexagram-number)
    [:br]
    (str "-- " (hex-gen/hexagram-name hexagram-number) " --")]
   [:h3 (str "Binary Representation: " (hex-gen/hexagram-bin hexagram-number))]
   [:div {:class "hexagram-svg-container"
          :style
          {:width  (* @scale-factor 33)
           :height "auto"}}
    (hexagram-svg hexagram-number)]])

(defn scale-slider
  [min max step]
  [:div (str
          (cond
            (int? step)   "integer"
            (float? step) "float")
          " " min "-" max ": ")
   [:br]
   [:input {:type      "range"
            :min       min
            :max       max
            :step      step
            :value     @scale-factor
            :on-change #(reset! scale-factor (-> % .-target .-value))}]])

(defn number-input
  [title atom]
  [:div title
   [:br]
   [:input {:type      "text"
            :value     @atom
            :on-change #(reset! atom (-> % .-target .-value))}]])



(defn ui []
  [:div {:style #js {:padding-left  "1rem"
                     :padding-right "1rem"
                     :display       "grid"}}
   [:h1 "I-ching Hexagram Generator"]
   [:div {:style #js {:display        "flex"
                      :flex           "0 1 auto"
                      :flex-direction "row"}}
    [:div
     [:p "Scale factor:"]
     [scale-slider 1 100 1]
     [scale-slider 0 10 1e-3]
     [number-input "Any number:" scale-factor]
     [:br]
     [number-input "Hexagram number (1-64):" hexagram-number]
     [:button
      {:on-click #(reset! hexagram-number (inc (rand-int 64)))}
      "Generate Random Hexagram"]
     (if (valid? @hexagram-number)
       nil
       [:p {:style #js
            {:color        "darkred"
             :border-style "solid"
             :border-color "red"
             :padding      "0.5rem"}}
        "Invalid input:"
        [:br]
        "Please put in a number from 1-64."])
     ]

    [:div {:style #js {:margin-left    "3rem"
                       :flex-grow      "1"
                       :padding-left   "1rem"
                       :padding-right  "1rem"
                       :padding-bottom "1rem"
                       :border-style   "solid"
                       :border-width   "2px"
                       :border-color   "black"}}
     (if (valid? @hexagram-number)
       (do (reset! last-valid-number @hexagram-number)
           (hexagram-info @hexagram-number))
       (hexagram-info @last-valid-number))]
    ]])

(defn mount-root
  [component]
  (r.dom/render component (.getElementById js/document "app")))

(defn ^:dev/after-load start
  []
  (.log js/console "start")
  (mount-root [ui]))

(defn ^:export init
  []
  (.log js/console "init")
  (start))

(defn ^:dev/before-load stop
  []
  (.log js/console "stop"))
