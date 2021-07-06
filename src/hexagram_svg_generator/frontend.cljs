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

(defn scale-slider
  [min max step]
  [:div (str
          (cond
            (int? step)   "Integer"
            (float? step) "Float"
            :else         (str "Step is of type: " (type step)))
          " " min "-" max ": ")
   [:br]
   [:input {:type      "range"
            :min       min
            :max       max
            :step      step
            :value     @scale-factor
            :on-change #(reset! scale-factor (-> % .-target .-value))}]])

(def step "fuckerfuck")

(defn number-input
  [title atom]
  [:div
   [:p {:class ["pb-1" ]} title]
   [:input {:type      "text"
            :value     @atom
            :on-change #(reset! atom (-> % .-target .-value))
            :class     ["dark:text-black"
                        "border-current" "border"
                        "filter" "drop-shadow-lg"
                        "rounded-md" "p-2"]}]])

(defn svg-container
  [hexagram-number]
  [:div {:id    "svg-container"
         :class ["m-4" "justify-self-center" "sm:justify-self-start"]
         :style
         {:width (* @scale-factor 33)}}
   (hexagram-svg hexagram-number)])

(defn hexagram-info
  [hexagram-number]
  [:div {:class ["text-2xl"
                 "border-solid"
                 "border-current"
                 "border-2"
                 "p-8" "pb-2"]}
   [:h2 {:class ["text-4xl" "font-bold"]}
    "Hexagram " [:br {:class ["sm:hidden"]}] (str "#" hexagram-number)]
   [:h3
    (str (hex-gen/hexagram-name hexagram-number))]
   [:br]
   [:h3 (str "Binary Representation: ")]
   [:p {:class "font-mono"}
    (hex-gen/hexagram-bin hexagram-number)]
   [svg-container hexagram-number]])


(defn titlebar
  []
  [:div {:class ["flex" "items-center"
                 "justify-between"
                 "font-medium" "text-4xl"
                 "p-8"]}
   [:h1 {:class ["min-w-min"]}
    "I-ching Hexagram Generator"]
   [:h1 {:class ["text-5xl"
                 "font-mono"
                 "pl-2"]}
    "☯"]])


(defn ui []
  [:div {:class ["dark:text-gray-200"]}
   [titlebar]
   [:div {:class ["grid"
                  "justify-center"
                  "sm:justify-start"
                  "sm:grid-flow-col"
                  "grid-flow-row"]}
    [:div {:class ["sm:pl-8" "sm:pr-4"
                   "px-8"]}
     [:h3 {:class ["text-2xl"]}
      "Scale factor:"]
     [number-input "Any number:" scale-factor]
     [:br]
     [scale-slider 1 100 1]
     [scale-slider 0 10 1e-3]
     [number-input "Hexagram number (1-64):" hexagram-number]
     [:button
      {:class ["border-current" "border"
               "p-2" "my-4"
               "w-48"
               "rounded-md" "bg-blue-300"
               "dark:bg-blue-600"]
       :on-click #(reset! hexagram-number (inc (rand-int 64)))}
      "Generate Random Hexagram"]

     (if (valid? @hexagram-number)
       nil
       [:p {:class ["text-red-500" "p-2"
                    "border-2" "border-red-700"]}
        "Invalid input:"
        [:br]
        "Please put in a number from 1-64."])]

    [:div {:class ["pl-4" "pr-8"]}
     (if (valid? @hexagram-number)
       (do (reset! last-valid-number @hexagram-number)
           (hexagram-info @hexagram-number))
       (hexagram-info @last-valid-number))]]])

;; This is super hacky but it works
(defn set-body-styles []
  (let [body js/document.body]
    (.add (.-classList body) "dark:bg-gray-900")))

(defn mount-root
  [component]
  (r.dom/render component (.getElementById js/document "app"))
  (set-body-styles))

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
