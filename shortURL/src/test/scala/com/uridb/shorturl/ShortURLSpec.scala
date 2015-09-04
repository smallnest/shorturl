package com.uridb.shorturl

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class ShortURLSpec extends FlatSpec with BeforeAndAfterEach with LazyLogging {
  override def beforeEach = {
  }

  //behavior of "ShortURL"

  "ShortURL" should "encode/decode a URL" in {
    var shortURL = ShortURL.encode(1234567890)
    var num = ShortURL.decode(shortURL)
    assert(num == 1234567890)

    shortURL = ShortURL.encode(63)
    num = ShortURL.decode(shortURL)
    assert(num == 63)

    shortURL = ShortURL.encode(64)
    num = ShortURL.decode(shortURL)
    assert(num == 64)

    shortURL = ShortURL.encode(262143)
    num = ShortURL.decode(shortURL)
    assert(num == 262143)

    num = ShortURL.decode("~~~~")
    assert(num == 16777215)
    num = ShortURL.decode("aaaab")
    assert(num == 16777216)

    shortURL = ShortURL.encode(Int.MaxValue.toLong)
    assert(shortURL == "~~~~~b")
    shortURL = ShortURL.encode(Int.MaxValue.toLong + 1)
    assert(shortURL == "aaaaac")
    num = ShortURL.decode("aaaaab")
    assert(num == 1073741824)
    num = ShortURL.decode("aaaaab")
    assert(num == 1073741824)
    num = ShortURL.decode("aA0Aab")
    assert(num == 1080772224)
  }

  it should "check whether short URL is valid" in {
    var shortURL = "abcd"
    assert(true == ShortURL.isValid(shortURL))
    assert(ShortURL.decode(shortURL) > 0)

    shortURL = "aBsZ0Ts+"
    assert(true == ShortURL.isValid(shortURL))
    assert(ShortURL.decode(shortURL) > 0)

    shortURL = "aB~+0Ts+"
    assert(true == ShortURL.isValid(shortURL))
    assert(ShortURL.decode(shortURL) > 0)

    shortURL = "++++++++"
    assert(true == ShortURL.isValid(shortURL))
    assert(ShortURL.decode(shortURL) > 0)

    shortURL = "~~~~~~~~"
    assert(true == ShortURL.isValid(shortURL))
    assert(ShortURL.decode(shortURL) > 0)

    shortURL = "abcdefgh"
    assert(true == ShortURL.isValid(shortURL))
    assert(ShortURL.decode(shortURL) > 0)

    shortURL = "abcdefgh9"
    assert(false == ShortURL.isValid(shortURL))

    var chars = "!@#$%^&:*;<>?/\\-="
    for (c <- chars) {
      assert(false == ShortURL.isValid("aB0cD+~" + c))
    }


  }
}
