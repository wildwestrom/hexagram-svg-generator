(ns hexagram-svg-generator.frontend
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as r.dom]
   [hexagram-svg-generator.hex-gen :as hex-gen]))

(defonce hexagram-number
  (reagent/atom "1"))

(defonce last-valid-number
  (reagent/atom "1"))

(defonce scale-factor
  (reagent/atom "5"))

(defn valid? [num]
  (and (<= 1 num)
       (>= 64 num)
       (not (nil? num))))

(defn svg-container
  [hexagram-number & {:keys [scale-factor]}]
  [:div {:id    "svg-container"
         :class ["m-4"
                 "justify-self-center"
                 "sm:justify-self-start"]
         :style
         {:width (* scale-factor 33)}}
   (hex-gen/generate-svg hexagram-number)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Begin Icon Component
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sm? [window-width] (<= window-width 640))

(def window-width
  (reagent/atom js/window.innerWidth))

(defn icon
  []
  (cond
    (sm? @window-width)       [svg-container @hexagram-number
                               :scale-factor 1]
    (not (sm? @window-width)) "☯"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End Icon Component
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn titlebar
  []
  [:div {:class ["flex" "items-center"
                 "justify-between"
                 "font-medium" "text-4xl"
                 "p-8"]}
   [:h1 {:class ["min-w-min"]}
    "I-ching Hexagram Generator"]
   [:div {:class ["text-5xl"
                  "font-mono"
                  "pl-2"]}
    (set! (.-onresize js/window)
          #(reset! window-width js/window.innerWidth))
    [icon]]])

(defn scale-slider
  [min max step]
  [:div (str (cond
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
            :on-change #(reset! scale-factor (-> % .-target .-value))
            :class     ["min-w-full"]}]])

(defn number-input
  [title atom rejection-message]
  [:div
   [:p {:class ["pb-1"]} title]
   [:input {:type        "text"
            :placeholder "Whole number from 1-64."
            :value       @atom
            :on-change   #(reset! atom (-> % .-target .-value))
            :class       ["dark:text-black"
                          "border-current" "border"
                          "min-w-full"
                          "filter" "drop-shadow-lg"
                          "rounded-md" "p-1" "mb-2"]}]
   rejection-message])

(defn random-hexagram-button
  []
  [:button
   {:class    ["border-current" "border"
               "p-2" "my-2"
               "filter" "drop-shadow-lg"
               "min-w-full"
               "rounded-md" "bg-blue-200"
               "dark:bg-blue-600"]
    :on-click #(reset! hexagram-number (inc (rand-int 64)))}
   "Generate Random Hexagram"])

(defn hexagram-info-box
  [hexagram-number]
  [:div {:class ["text-2xl"
                 "border-solid"
                 "border-current"
                 "border-2"
                 "p-8" "pb-2" "min-w-60"]}
   [:h2 {:class ["text-4xl" "font-bold"]}
    (str "Hexagram #" hexagram-number)]
   [:h3
    (str (hex-gen/hexagram-name hexagram-number))]
   [:br]
   [:h3 (str "Binary Representation: ")]
   [:p {:class "font-mono"}
    (hex-gen/hexagram-bin hexagram-number)]
   [svg-container hexagram-number :scale-factor @scale-factor]])

(defn control-panel
  []
  [:div {:class ["sm:place-self-start"
                 "sm:w-1/3"
                 "place-self-center"
                 "min-w-full"]}
   [:h3 {:class ["text-2xl"]}
    "Hexagram Number:"]
   [number-input "" hexagram-number
    ;;
    [:p {:class [(if (valid? @hexagram-number)
                   "hidden" nil)
                 "text-red-500" "p-2"
                 "border-2" "border-red-700"]}
     "Invalid input:"
     [:br]
     "Please put in a number from 1-64."]]

   [random-hexagram-button]
   [:h3 {:class ["text-2xl"]}
    "Scale factor:"]
   [number-input "" scale-factor nil]
   [scale-slider 1 100 1]
   [scale-slider 0 10 1e-3]])

(defn ui []
  [:div {:class ["dark:text-gray-200"]}
   [titlebar]
   [:div {:class ["grid"
                  "sm:justify-start"
                  "sm:grid-flow-col"
                  "w-screen"
                  "gap-8" "px-8"
                  "grid-flow-row"]}
    [control-panel]
    [:div {:class ["sm:pr-8"
                   "min-w-2/3"
                   ;; "place-self-stretch"
                   ]}
     (if (valid? @hexagram-number)
       (do (reset! last-valid-number @hexagram-number)
           (hexagram-info-box @hexagram-number))
       (hexagram-info-box @last-valid-number))]]])

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
