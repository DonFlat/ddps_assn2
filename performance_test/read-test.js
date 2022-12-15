import http from 'k6/http'
import { sleep } from 'k6'

let fileName = 'hello-world'


/*  */
export default function () {
    http.get(`http://node104:2206/file/${fileName}/metadata`)
}