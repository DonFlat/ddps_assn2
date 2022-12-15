import http from 'k6/http'

export default function () {

    // Obtain leases
    const masterNode = 'localhost'
    const fileName = 'hello-there'

    const getLeaseUrl = `http://${masterNode}:2206/${fileName}/lease`

    const chunkservers = http.get(getLeaseUrl)
    console.log(chunkservers.body)

    // Write to chunkserver
    const content = 'This part sent from the k6 test'
    if (chunkservers.length === 1) {
        const writeFileUrl = `http://${chunkservers[0]}:2206/file/${fileName}/content/${3}`
        http.post(writeFileUrl, content)
    }
    if (chunkservers.length === 2) {
        const writeFileUrl0 = `http://${chunkservers[0]}:2206/file/${fileName}/content/${2}`
        http.post(writeFileUrl0, content)

        const writeFileUrl1 = `http://${chunkservers[1]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl1, content)
    }
    if (chunkservers.length >= 3) {
        const writeFileUrl0 = `http://${chunkservers[0]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl0, content)

        const writeFileUrl1 = `http://${chunkservers[1]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl1, content)

        const writeFileUrl2 = `http://${chunkservers[2]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl2, content)
    }
}