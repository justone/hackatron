(ns hackatron.common-test
  (:require [clojure.test :refer :all]
            [hackatron.common :refer :all]))

(deftest email-validation
  (testing "bad emails"
    (is (not (valid-email? "foobar")))
    (is (not (valid-email? "foobar@")))
    (is (not (valid-email? "@foobar"))))
  (testing "good enough emails"
    (is (valid-email? "bob@somewhere.com"))
    (is (valid-email? "bob@somewhere"))
    (is (valid-email? "bob@google.com"))))
