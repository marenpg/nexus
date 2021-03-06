package ch.epfl.bluebrain.nexus.rdf.iri

import cats.Eq
import cats.syntax.show._
import ch.epfl.bluebrain.nexus.rdf.RdfSpec
import ch.epfl.bluebrain.nexus.rdf.iri.Authority.{Host, Port, UserInfo}
import io.circe.Json
import io.circe.syntax._

class AuthoritySpec extends RdfSpec {
  "An Authority" should {
    // format: off
    val values = List(
      "user:pass@1.1.1.1:8080"  -> Authority(Some(UserInfo("user:pass").rightValue), Host.ipv4("1.1.1.1").rightValue, Some(Port(8080).rightValue)),
      "google.com"              -> Authority(None, Host.named("google.com").rightValue, None),
      "user:£¤¥@epfl.ch"        -> Authority(Some(UserInfo("user:£¤¥").rightValue), Host.named("epfl.ch").rightValue, None)
    )
    // format: on
    "be constructed successfully" in {
      forAll(values) {
        case (string, authority) =>
          Authority(string).rightValue shouldEqual authority
          authority.iriString shouldEqual string
          authority.show shouldEqual string
      }
    }

    "eq" in {
      val upper = Authority(None, Host.named("EPFL.CH").rightValue, None)
      val lower = Authority(None, Host.named("epfl.ch").rightValue, None)
      Eq.eqv(upper, lower) shouldEqual true
    }

    "encode" in {
      forAll(values) {
        case (string, authority) => authority.asJson shouldEqual Json.fromString(string)
      }
    }
    "decode" in {
      forAll(values) {
        case (string, authority) => Json.fromString(string).as[Authority].rightValue shouldEqual authority
      }
    }
  }

}
