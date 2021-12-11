import yaml,os

def getBank(bankid):
    usebank, bankname = None, None
    with open(os.getcwd() + "/aassist/config.yml", "r") as config:
        cfg = yaml.safe_load(config)
        banks = cfg['banks']
        for key, bank in banks.items():
            id = bank['uid']
            if str(id) == str(bankid):
                usebank = key
                bankname = bank['name']
    return [usebank,bankname]