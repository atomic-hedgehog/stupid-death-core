(ns stupid-death.characters)

; character creation
;;  select weapon - axe, sword, knife, hammer, flamethrower, hickory wand
;;  select talent - sneaky, strong, fast, smart
;;  health levels - hit locations on organs?

(def characters (atom {}))

(defn create-character [player-name weapon talent]
  (swap! merge characters {player-name
  {:player-name player-name
   :weapon weapon
   :talent talent
   :conditions []
   :status :healthy
   :inventory {weapon 1}}})

(defn take-item [player-name item quantity]
)

