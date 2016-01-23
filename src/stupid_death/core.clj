(ns stupid-death.core
  (:gen-class))

(defn log [message]
  (println "> " message))

(def body-parts ["chest" "stomach" "head" "left arm" "left leg" "right arm" "right leg" "groin" "neck" "right eye" "mouth" "left eye" "nose" "hair"])

(defn random-body-part []
  (rand-nth body-parts))

(defn random-character [world]
  (let [characters (-> @world :party :characters)
        live-characters (filter (fn [[n {:keys [status]}]]
                                     (= status :healthy)) 
                                characters)]
    (rand-nth live-characters)))

(defn kill-character [world character-name]
  (swap! world (fn [w]
                 (update-in w [:party :characters character-name :status] (fn [_] :dead)))))


(defn weasel-kill [world]
  (let [[victim _] (random-character world)
        entrance-wound (random-body-part)
        exit-wound (random-body-part)]
    (kill-character world victim)
    (log (str victim ", a sponge weasel leaps from the pile, yellow teeth snarling, and chews it's way into your " entrance-wound ".  After a gruesome gurlging and chewing and fountains of blood, it emerges sated from your " exit-wound))))

(defn entrance-kill [world]
  (let [[victim _] (random-character world)
        entrance-wound (random-body-part)
        exit-wound (random-body-part)]

    (kill-character world victim)
    (log (str victim ", a loose rock falls from the ceiling and crushes your " entrance-wound ".  After twitching in pain for twenty minutes you finally succumb to internal bleeding"))))

(defn witch-kill [world]
  (let [[victim _] (random-character world)
        entrance-wound (random-body-part)
        exit-wound (random-body-part)]

    (kill-character world victim)
    (log (str victim 
              ", a foul witch leans around a shelf and fires some kind of wand at you.  The bolt hits your " 
              entrance-wound 
              " and turns it into a venomous snake!. The snake curls around and bites your " 
              exit-wound 
              ", injecting acidic venom.  You scream and convulse for 10 minutes before your spine breaks, killing you."))))

(def world 
  (atom 
    {:party {:location :entrance
             :characters {"andy g" {:inventory []
                                    :conditions []
                                    :status :healthy}
                          "ben rr" {:inventory []
                                    :conditions []
                                    :status :healthy}
                          "eric g" {:inventory []
                                    :conditions []
                                    :status :healthy}
                          
                          "karl k" {:inventory []
                                  :conditions []
                                  :status :healthy}}}
     :objects {"spongeweasel dung" {:description "disgusting spongeweasel dung" 
                                    :location :spongeweasels 
                                    :weight 1 
                                    :owned-by nil}
               "2x4" {:description "2x4 with splinters and nails" 
                      :location :entrance 
                      :weight 1 
                      :owned-by nil}}
     :rooms {:witch-hovel
             {:description "a low room, with the skins of many animals hanging from the ceiling.  In the center of the room is a bubbling cauldron, and the permiter is covered with shelves containing glass jars containing vile substances"
              :kill-fn witch-kill
              :exits {:west {:goes-to :entrance
                             :description "fresh air wafts from a waist-high hole in the wall"}}}
             
             :entrance
             {:description "an empty mausoleum.  There are stairs going down"
              :kill-fn entrance-kill
              :exits {:down {:goes-to :spongeweasels
                             :description "A dark staircase, reeking with the stench of weasel"}
                      :east {:goes-to :witch-hovel
                             :description "A low hole in the wall, with fragrant smoke wafting out"}}}
             :spongeweasels
             {:description "A pit filled with squirming spongeweasels!"
              :kill-fn weasel-kill
              :exits {:up {:goes-to :entrance
                           :description "A staircase, with a cool breeze of fresh air wafting down"}}}}}))



(defn describe-room [world location-name]
  (-> @world :rooms location-name :description log))

(defn- object-location [world object-name]
  (let [object ((:objects @world) object-name)]
    (:location object)))
    
(defn describe-objects [world location-name]
  (doseq [[object-name object] (:objects @world)]
    (if (and
          (nil? (:owned-by object))
          (= (object-location world object-name) location-name))
      (log (str "you see " (:description object) " here")))))

(defn describe-exits [world location-name]
  (let [exits (-> @world :rooms location-name :exits)]
    (doseq [[exit-direction exit] exits]
      (log (str (:description exit) " leads " (name exit-direction))))))

(defn describe-location [world location-name]
  (describe-room world location-name)
  (describe-objects world location-name)
  (describe-exits world location-name))

(defn attack [world attacker defender]
  )

(defn pickup [world object-name]
  )

(defn can-move? [world direction]
  (let [party-location (-> @world :party :location)
        exits (-> @world :rooms party-location :exits)]
    (if (exits direction)
      true
      false)))
      

(defn look [world]
  (describe-location world (-> @world :party :location)))

(defn update-location [world direction]
  (let [current-room ((-> @world :rooms) (-> @world :party :location))
        new-room (-> current-room :exits direction :goes-to)]
    (swap! world (fn [w]
                   (update-in w [:party :location] (fn [_] new-room))))
    (look world)
    (when (< 30 (rand-int 100))
      (let [current-room ((-> @world :rooms) (-> @world :party :location))
            kill-fn (:kill-fn current-room)]
        (kill-fn world)))))
  

(defn move [world direction]
  (if (can-move? world direction)
    (do
      (log (str "heading " (name direction) "!"))
      (update-location world direction))
    (log (str "you can't go that way"))))

(defn -main

  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
