import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

def call(path) {
  def package_json = new File(path +'/package.json').text
  return new JsonSlurper().parseText(package_json)
}
