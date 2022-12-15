import http from 'k6/http'

export default function () {

    // Obtain leases
    const masterNode = 'node105'
    const fileName = 'hello-there'

    const getLeaseUrl = `http://${masterNode}:2206/${fileName}/lease`

    const chunkservers = http.get(getLeaseUrl)
    console.log(`chunkserver num: ${chunkservers.json()[0]}`)

    let servers = []
    // Write to chunkserver
    const content = 'This part sent from the k6 test'
    if (servers.length === 1) {
        const writeFileUrl = `http://${servers[0]}:2206/file/${fileName}/content/${3}`
        http.post(writeFileUrl, content)
    }
    if (servers.length === 2) {
        const writeFileUrl0 = `http://${servers[0]}:2206/file/${fileName}/content/${2}`
        http.post(writeFileUrl0, content)

        const writeFileUrl1 = `http://${servers[1]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl1, content)
    }
    if (servers.length >= 3) {
        const writeFileUrl0 = `http://${servers[0]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl0, content)

        const writeFileUrl1 = `http://${servers[1]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl1, content)

        const writeFileUrl2 = `http://${servers[2]}:2206/file/${fileName}/content/${1}`
        http.post(writeFileUrl2, content)
    }
}