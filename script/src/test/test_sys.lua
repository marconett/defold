function test_sys()
    local filename = "save001.save"
    local file = sys.get_save_file("my_game", filename)
    -- Get file again, test mkdir
    file = sys.get_save_file("my_game", filename)
    result, error = os.remove(file)

    local data = sys.load(file)
    assert(#data == 0)

    local data = { high_score = 1234, location = vmath.vector3(1,2,3), xp = 99, name = "Mr Player" }
    local result = sys.save(file, data)
    assert(result)

    local data_prim = sys.load(file)

    assert(data['high_score'] == data_prim['high_score'])
    assert(data['location'] == data_prim['location'])
    assert(data['xp'] == data_prim['xp'])
    assert(data['name'] == data_prim['name'])

    -- get_config
    assert(sys.get_config("main.does_not_exists") == nil)
    assert(sys.get_config("main.does_not_exists", "foobar") == "foobar")
    assert(sys.get_config("foo.value") == "123")
    assert(sys.get_config("foo.value", 456) == "123")
end

functions = { test_sys = test_sys }
